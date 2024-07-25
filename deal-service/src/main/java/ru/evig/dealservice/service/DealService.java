package ru.evig.dealservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.entity.Client;
import ru.evig.dealservice.entity.Credit;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.enums.ApplicationStatus;
import ru.evig.dealservice.enums.ChangeType;
import ru.evig.dealservice.enums.CreditStatus;
import ru.evig.dealservice.enums.EmailTheme;
import ru.evig.dealservice.exception.StatementDeniedException;
import ru.evig.dealservice.exception.StatementIssuedException;
import ru.evig.dealservice.mapper.ClientMapper;
import ru.evig.dealservice.mapper.CreditMapper;
import ru.evig.dealservice.mapper.PassportMapper;
import ru.evig.dealservice.mapper.ScoringDataMapper;
import ru.evig.dealservice.repository.ClientRepository;
import ru.evig.dealservice.repository.CreditRepository;
import ru.evig.dealservice.repository.StatementRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealService {
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;

    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;
    private final PassportMapper passportMapper;
    private final ScoringDataMapper sdMapper;

    private final KafkaService kafka;

    public Client createClient(LoanStatementRequestDto lsrDto) {
        log.info("Input data for createClient = " + lsrDto);

        PassportDto passport = passportMapper.lsrDtoToPassportDto(lsrDto);
        Client client = clientMapper.loanStatementRequestDtoToEntity(lsrDto);
        client = clientMapper.passportDtoToEntity(client, passport);

        clientRepository.save(client);

        log.info("Saved data to DB from createClient = " + client);

        return client;
    }

    public Statement createStatement(Client client) {
        Statement statement = Statement.builder()
                .clientId(client)
                .statusHistory(new ArrayList<>())
                .status(ApplicationStatus.PREAPPROVAL)
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from createStatement = " + statement);

        return statement;
    }

    public List<LoanOfferDto> getLoanOfferDtoList(List<LoanOfferDto> list, UUID id) {
        log.info("Input data for getLoanOfferDtoList = " + list + " " + id);

        for (LoanOfferDto loDto : list) {
            loDto.setStatementId(id);
        }

        return list;
    }

    public void selectLoanOffer(LoanOfferDto loDto) {
        checkDeniedStatus(loDto.getStatementId().toString());

        log.info("Input data for selectLoanOffer = " + loDto);

        Statement statement = findStatementInDB(loDto.getStatementId());
        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .statusHistory(getChangedStatementStatus(statement))
                .status(ApplicationStatus.APPROVED)
                .appliedOffer(loDto)
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from selectLoanOffer = " + statement);

        sendMessageToTopic(loDto.getStatementId().toString(),
                EmailTheme.FINISH_REGISTRATION,
                "finish-registration"
        );
    }

    public ScoringDataDto getScoringDataDto(FinishRegistrationRequestDto frrDto, String statementId) {
        log.info("Input data for getScoringDto = " + frrDto + " and " + statementId);

        Statement statement = findStatementInDB(UUID.fromString(statementId));
        ScoringDataDto sdDto = sdMapper.frrDtoToSdDto(frrDto, statement);

        log.info("Output data from getScoringDataDto = " + sdDto);

        return sdDto;
    }

    public void completeBuildingClient(FinishRegistrationRequestDto frrDto, String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));

        PassportDto passport = statement.getClientId().getPassport();
        passport = passportMapper.frrDtoToPassportDto(frrDto, passport);

        Client client = statement.getClientId();
        client = clientMapper.finishRegistrationDtoToEntity(client, frrDto);
        client = clientMapper.passportDtoToEntity(client, passport);

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .clientId(client)
                .build();

        clientRepository.save(client);
        statementRepository.save(statement);

        log.info("Saved data to DB from completeBuildingClient = " + statement);
    }

    public void createCredit(CreditDto cDto, FinishRegistrationRequestDto frrDto, String statementId) {
        log.info("Input data for createCredit = " + cDto + " " + frrDto + " " + statementId);

        completeBuildingClient(frrDto, statementId);

        Credit credit = creditMapper.dtoToEntity(cDto);
        credit = creditMapper.statusToEntity(credit, CreditStatus.CALCULATED);

        Statement statement = findStatementInDB(UUID.fromString(statementId));
        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .creditId(credit)
                .build();

        creditRepository.save(credit);
        statementRepository.save(statement);

        changeStatementStatus(statementId, ApplicationStatus.CC_APPROVED);

        log.info("Saved credit to DB from createCredit = " + credit);
        log.info("Saved statement to DB from createCredit = " + statement);

        sendMessageToTopic(statementId,
                EmailTheme.SEND_DOCUMENTS,
                "send-documents"
        );
    }

    public Statement findStatementInDB(UUID id) {
        Statement statement;
        Optional<Statement> statementOptional = statementRepository.findById(id);

        if (statementOptional.isPresent()) {
            statement = statementOptional.get();
        } else {
            throw new EntityNotFoundException("Statement with ID " + id + " not found");
        }

        return statement;
    }

    public List<Statement> findAllStatementsInDB() {

        return statementRepository.findAll();
    }

    private List<StatementStatusHistoryDto> getChangedStatementStatus(Statement statement) {
        StatementStatusHistoryDto sshDto = StatementStatusHistoryDto.builder()
                .status(statement.getStatus())
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();

        List<StatementStatusHistoryDto> list = statement.getStatusHistory();
        list.add(sshDto);

        return list;
    }

    public String generateSesCode(String statementId) {
        String firstPart = statementId.split("-")[0].replace("-", "");
        String secondPart = String.format("%d%d%d",
                LocalDate.now().getDayOfMonth(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear()
        );
        Random r = new Random();
        int x = r.nextInt(100) + 1;
        String thirdPart = String.valueOf(x);

        String sesCode = firstPart + secondPart + thirdPart;

        Statement statement = findStatementInDB(UUID.fromString(statementId));
        statement.setSesCode(sesCode);

        statementRepository.save(statement);

        log.info("Generated SES-code from generateSesCode = " + sesCode);

        return sesCode;
    }

    public void documentPerforming(String statementId) {
        changeStatementStatus(statementId, ApplicationStatus.PREPARE_DOCUMENTS);

        sendMessageToTopic(statementId,
                EmailTheme.CREATE_DOCUMENTS,
                "create-documents"
        );

        changeStatementStatus(statementId, ApplicationStatus.DOCUMENT_CREATED);
    }

    public void sesCodePerforming(String statementId) {
        checkDeniedStatus(statementId);
        generateSesCode(statementId);

        sendMessageToTopic(statementId,
                EmailTheme.SEND_SES,
                "send-ses"
        );
    }

    public void finishOperation(String statementId) {
        changeStatementStatus(statementId, ApplicationStatus.DOCUMENT_SIGNED);
        signDocuments(statementId);
        changeStatementStatus(statementId, ApplicationStatus.CREDIT_ISSUED);
        creditStatusIssued(statementId);

        sendMessageToTopic(statementId,
                EmailTheme.CREDIT_ISSUED,
                "credit-issued"
        );
    }

    public void denialPerforming(String statementId) {
        checkDeniedStatus(statementId);
        checkIssuedStatus(statementId);

        changeStatementStatus(statementId, ApplicationStatus.CLIENT_DENIED);

        sendMessageToTopic(statementId,
                EmailTheme.STATEMENT_DENIED,
                "statement-denied"
        );
    }

    public void signDocuments(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        statement.setSignDate(LocalDateTime.now());

        Statement saved = statementRepository.save(statement);

        log.info("Saved data to DB from signDocuments = " + saved);
    }

    public void creditStatusIssued(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));

        Credit credit = statement.getCreditId();
        credit = creditMapper.statusToEntity(credit, CreditStatus.ISSUED);

        creditRepository.save(credit);
    }

    public void checkDeniedStatus(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));

        if (statement.getStatus().equals(ApplicationStatus.CLIENT_DENIED)) {
            throw new StatementDeniedException("Statement with ID " + statementId +
                    " cannot be processed further");
        }
    }

    public void checkIssuedStatus(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));

        if (statement.getStatus().equals(ApplicationStatus.CREDIT_ISSUED)) {
            throw new StatementIssuedException("Statement with ID " + statementId +
                    " already issued");
        }
    }

    public void changeStatementStatus(String statementId, ApplicationStatus status) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .statusHistory(getChangedStatementStatus(statement))
                .status(status)
                .build();

        statementRepository.save(statement);
    }

    public void sendMessageToTopic(String statementId, EmailTheme theme, String topic) {
        UUID id = UUID.fromString(statementId);
        Statement statement = findStatementInDB(id);

        EmailMessage message = EmailMessage.builder()
                .address(statement.getClientId().getEmail())
                .theme(theme)
                .statementId(id)
                .build();

        kafka.sendMessageToTopic(topic, message);
    }
}
