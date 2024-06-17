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
import ru.evig.dealservice.exception.DtoNotFoundException;
import ru.evig.dealservice.exception.TooYoungForCreditException;
import ru.evig.dealservice.repository.ClientRepository;
import ru.evig.dealservice.repository.CreditRepository;
import ru.evig.dealservice.repository.StatementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DealService {
    private final Integer MIN_AGE = 18;

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private Statement statement;
    private Client client;
    private PassportDto passport;

    public void createClient(LoanStatementRequestDto lsrDto) {
        log.info("Input data for createClient = " + lsrDto);

        if (LocalDate.from(lsrDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) < MIN_AGE) {
            throw new TooYoungForCreditException("The person is younger than 18");
        }

        passport = PassportDto.builder()
                .passportUUID(UUID.randomUUID())
                .series(lsrDto.getPassportSeries())
                .number(lsrDto.getPassportNumber())
                .build();

        client = Client.builder()
                .id(UUID.randomUUID())
                .lastName(lsrDto.getLastName())
                .firstName(lsrDto.getFirstName())
                .middleName(lsrDto.getMiddleName())
                .birthDate(lsrDto.getBirthday())
                .email(lsrDto.getEmail())
                .passport(passport)
                .build();

        clientRepository.save(client);

        log.info("Saved data to DB from createClient = " + client);

        createStatement();
    }

    public List<LoanOfferDto> getLoanOfferDtoList(List<LoanOfferDto> list) {
        log.info("Input data for getLoanOfferDtoList = " + list);

        for (LoanOfferDto loDto : list) {
            loDto.setStatementId(statement.getId());
        }

        log.info("Output data from getLoanOfferDtoList = " + list);

        return list;
    }

    public void selectLoanOffer(LoanOfferDto loDto) {
        log.info("Input data for selectLoanOffer = " + loDto);

        Optional<Statement> statementOptional = statementRepository.findById(loDto.getStatementId());

        if (statementOptional.isPresent()) {
            statement = statementOptional.get();
        } else {
            throw new DtoNotFoundException("Statement with ID " + loDto.getStatementId() + " not found");
        }

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .status(ApplicationStatus.APPROVED)
                .statusHistory(getChangedStatementStatus(statement, ChangeType.MANUAL))
                .appliedOffer(loDto)
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from selectLoanOffer = " + statement);
    }

    public ScoringDataDto getScoringDataDto(FinishRegistrationRequestDto frrDto, String statementId) {
        log.info("Input data for getScoringDto = " + frrDto + " and " + statementId);

        Optional<Statement> statementOptional = statementRepository.findById(UUID.fromString(statementId));

        if (statementOptional.isPresent()) {
            statement = statementOptional.get();
        } else {
            throw new DtoNotFoundException("Statement with ID " + UUID.fromString(statementId) + " not found");
        }

        completeBuildingClient(frrDto);

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

    public void makeCredit(CreditDto cDto) {
        log.info("Input data for makeCredit = " + cDto);

        Credit credit = Credit.builder()
                .id(UUID.randomUUID())
                .amount(cDto.getAmount())
                .term(cDto.getTerm())
                .monthlyPayment(cDto.getMonthlyPayment())
                .rate(cDto.getRate())
                .psk(cDto.getPsk())
                .paymentSchedule(cDto.getPaymentSchedule())
                .insuranceEnabled(cDto.getIsInsuranceEnabled())
                .salaryClient(cDto.getIsSalaryClient())
                .creditStatus(CreditStatus.CALCULATED)
                .build();

        creditRepository.save(credit);

        Statement.StatementBuilder statementToBuilder = statement.toBuilder();
        statement = statementToBuilder
                .statusHistory(getChangedStatementStatus(statement, ChangeType.AUTOMATIC))
                .creditId(credit)
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from makeCredit = " + statement);
    }

    private void createStatement() {
        statement = Statement.builder()
                .id(UUID.randomUUID())
                .clientId(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(new ArrayList<>())
                .build();

        statementRepository.save(statement);

        log.info("Saved data to DB from createStatement = " + statement);
    }

    private void completeBuildingClient(FinishRegistrationRequestDto frrDto) {
        PassportDto.PassportDtoBuilder passportToBuilder = passport.toBuilder();
        passport = passportToBuilder
                .issueDate(frrDto.getPassportIssueDate())
                .issueBranch(frrDto.getPassportIssueBranch())
                .build();

        Client.ClientBuilder clientToBuilder = client.toBuilder();
        client = clientToBuilder
                .gender(frrDto.getGender())
                .maritalStatus(frrDto.getMaritalStatus())
                .dependentAmount(frrDto.getDependentAmount())
                .passport(passport)
                .employment(frrDto.getEmployment())
                .accountNumber(frrDto.getAccountNumber())
                .build();

        clientRepository.save(client);

        log.info("Saved data to DB from completeBuildingClient = " + client);
    }

    private List<StatementStatusHistoryDto> getChangedStatementStatus(Statement statement, ChangeType changeType) {
        StatementStatusHistoryDto sshDto = StatementStatusHistoryDto.builder()
                .status(statement.getStatus())
                .time(LocalDateTime.now())
                .changeType(changeType)
                .build();

        List<StatementStatusHistoryDto> list = statement.getStatusHistory();
        list.add(sshDto);

        return list;
    }
}
