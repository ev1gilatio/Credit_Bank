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
import ru.evig.dealservice.mapper.ClientMapper;
import ru.evig.dealservice.mapper.CreditMapper;
import ru.evig.dealservice.repository.ClientRepository;
import ru.evig.dealservice.repository.CreditRepository;
import ru.evig.dealservice.repository.StatementRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealService {
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;

    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;

    public Client createClient(LoanStatementRequestDto lsrDto) {
        log.info("Input data for createClient = " + lsrDto);

        PassportDto passport = PassportDto.builder()
                .passportUUID(UUID.randomUUID())
                .series(lsrDto.getPassportSeries())
                .number(lsrDto.getPassportNumber())
                .build();

        Client client = clientMapper.loanStatementRequestDtoToEntity(lsrDto);
        client.setPassport(passport);

        clientRepository.save(client);

        log.info("Saved data to DB from createClient = " + client);

        return client;
    }

    public Statement createStatement(Client client) {
        Statement statement = Statement.builder()
                .id(UUID.randomUUID())
                .clientId(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(new ArrayList<>())
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from createStatement = " + statement);

        return statement;
    }

    public List<LoanOfferDto> getLoanOfferDtoList(List<LoanOfferDto> list, UUID id) {
        log.info("Input data for getLoanOfferDtoList = " + list);

        for (LoanOfferDto loDto : list) {
            loDto.setStatementId(id);
        }

        log.info("Output data from getLoanOfferDtoList = " + list);

        return list;
    }

    public void selectLoanOffer(LoanOfferDto loDto) {
        log.info("Input data for selectLoanOffer = " + loDto);

        Statement statement = findStatementInDB(loDto.getStatementId());

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .status(ApplicationStatus.APPROVED)
                .statusHistory(getChangedStatementStatus(statement))
                .appliedOffer(loDto)
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from selectLoanOffer = " + statement);
    }

    public ScoringDataDto getScoringDataDto(FinishRegistrationRequestDto frrDto, String statementId) {
        log.info("Input data for getScoringDto = " + frrDto + " and " + statementId);

        Statement statement = findStatementInDB(UUID.fromString(statementId));

        ScoringDataDto sdDto = ScoringDataDto.builder()
                .amount(statement.getAppliedOffer().getTotalAmount())
                .term(statement.getAppliedOffer().getTerm())
                .firstName(statement.getClientId().getFirstName())
                .lastName(statement.getClientId().getLastName())
                .middleName(statement.getClientId().getMiddleName())
                .gender(frrDto.getGender())
                .birthday(statement.getClientId().getBirthDate())
                .passportSeries(statement.getClientId().getPassport().getSeries())
                .passportNumber(statement.getClientId().getPassport().getNumber())
                .passportIssueDate(frrDto.getPassportIssueDate())
                .passportIssueBranch(frrDto.getPassportIssueBranch())
                .maritalStatus(frrDto.getMaritalStatus())
                .dependentAmount(frrDto.getDependentAmount())
                .employment(frrDto.getEmployment())
                .accountNumber(frrDto.getAccountNumber())
                .isInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnable())
                .isSalaryClient(statement.getAppliedOffer().getIsSalaryClient())
                .build();

        log.info("Output data from getScoringDataDto = " + sdDto);

        return sdDto;
    }

    public void completeBuildingClient(FinishRegistrationRequestDto frrDto, String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        PassportDto passport = statement.getClientId().getPassport();

        PassportDto.PassportDtoBuilder passportToBuilder = passport.toBuilder();
        passport = passportToBuilder
                .issueDate(frrDto.getPassportIssueDate())
                .issueBranch(frrDto.getPassportIssueBranch())
                .build();

        Client oldClient = statement.getClientId();
        Client updatedClient = clientMapper.finishRegistrationDtoToEntity(oldClient, frrDto);
        updatedClient.setPassport(passport);

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .clientId(updatedClient)
                .build();

        clientRepository.save(updatedClient);
        statementRepository.save(statement);

        log.info("Saved data to DB from completeBuildingClient = " + statement);
    }

    public void createCredit(CreditDto cDto, String statementId) {
        log.info("Input data for createCredit = " + cDto + " " + statementId);

        Credit credit = creditMapper.dtoToEntity(cDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);

        Statement statement = findStatementInDB(UUID.fromString(statementId));

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .statusHistory(getChangedStatementStatus(statement))
                .creditId(credit)
                .build();

        creditRepository.save(credit);
        statementRepository.save(statement);

        log.info("Saved credit to DB from createCredit = " + credit);
        log.info("Saved statement to DB from createCredit = " + statement);
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
}
