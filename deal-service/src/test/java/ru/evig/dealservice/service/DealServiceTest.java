package ru.evig.dealservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.entity.Client;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.enums.*;
import ru.evig.dealservice.mapper.ClientMapper;
import ru.evig.dealservice.repository.ClientRepository;
import ru.evig.dealservice.repository.StatementRepository;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DealServiceTest {

    @Autowired
    private DealService service;

    @Autowired
    private ClientMapper clientMapper;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private StatementRepository statementRepository;

    @Test
    void shouldReturnClient() {
        Client client = service.createClient(getLsrDto());

        assertNotNull(client);
        assertNotNull(client.getPassport());
        assertEquals("Vladimir", client.getFirstName());
        assertEquals("Petrov", client.getLastName());
        assertEquals("vov@vov.vov", client.getEmail());
        assertEquals("0123", client.getPassport().getSeries());
        assertEquals("456789", client.getPassport().getNumber());
    }

    @Test
    void shouldReturnStatement() {
        Client client = service.createClient(getLsrDto());
        Statement statement = service.createStatement(client);

        assertNotNull(statement);
        assertNotNull(statement.getId());
        assertNotNull(statement.getClientId());
        assertEquals(client, statement.getClientId());
        assertEquals(ApplicationStatus.PREAPPROVAL, statement.getStatus());
        assertEquals(new ArrayList<>(), statement.getStatusHistory());
        assertNull(statement.getCreditId());
    }

    @Test
    void shouldAddStatementIdToLoanOfferDto() {
        Client client = createClient(getLsrDto());
        Statement statement = createStatement(client);

        List<LoanOfferDto> list = service.getLoanOfferDtoList(
                getLoDtoList(),
                statement.getId()
        );

        assertNotNull(list);
        assertEquals(4, list.size());
        assertEquals(statement.getId(), list.get(0).getStatementId());
    }

    @Test
    void shouldReturnEntityNotFoundExceptionInsteadOfCredit() {
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> service.createCredit(getCreditDto(), "5d3ddd1d-eef2-414d-900f-a625669e5ad0")
        );

        assertTrue(thrown.getMessage().contains("Statement with ID 5d3ddd1d-eef2-414d-900f-a625669e5ad0 not found"));
    }

    @Test
    void shouldReturnEntityNotFoundExceptionInsteadOfScoringDataDto() {
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> service.getScoringDataDto(getFrrDto(), "5d3ddd1d-eef2-414d-900f-a625669e5ad0")
        );

        assertTrue(thrown.getMessage().contains("Statement with ID 5d3ddd1d-eef2-414d-900f-a625669e5ad0 not found"));
    }

    private Client createClient(LoanStatementRequestDto lsrDto) {
        PassportDto passport = PassportDto.builder()
                .passportUUID(UUID.randomUUID())
                .series(lsrDto.getPassportSeries())
                .number(lsrDto.getPassportNumber())
                .build();

        return Client.builder()
                .id(UUID.randomUUID())
                .lastName(lsrDto.getLastName())
                .firstName(lsrDto.getFirstName())
                .middleName(lsrDto.getMiddleName())
                .birthDate(lsrDto.getBirthday())
                .email(lsrDto.getEmail())
                .passport(passport)
                .build();
    }

    private Statement createStatement(Client client) {

        return Statement.builder()
                .id(UUID.randomUUID())
                .clientId(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(new ArrayList<>())
                .build();
    }

    private List<LoanOfferDto> getLoDtoList() {
        List<LoanOfferDto> list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            LoanOfferDto loDto = LoanOfferDto.builder()
                    .requestedAmount(new BigDecimal(30000))
                    .totalAmount(new BigDecimal(30000))
                    .term(6)
                    .monthlyPayment(new BigDecimal("5154.24"))
                    .rate(new BigDecimal("10.5"))
                    .isInsuranceEnable(false)
                    .isSalaryClient(false)
                    .build();

            list.add(loDto);
        }

        return list;
    }

    private LoanStatementRequestDto getLsrDto() {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName("Vladimir")
                .lastName("Petrov")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }

    private CreditDto getCreditDto() {

        return CreditDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .monthlyPayment(new BigDecimal("5109.94"))
                .rate(new BigDecimal("7.5"))
                .psk(new BigDecimal("30659.66"))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .paymentSchedule(null)
                .build();
    }

    private FinishRegistrationRequestDto getFrrDto() {

        return FinishRegistrationRequestDto.builder()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.NOT_MARRIED)
                .dependentAmount(-1)
                .passportIssueDate(LocalDate.now())
                .passportIssueBranch("NeoFlex SPb")
                .employment(null)
                .accountNumber("AccNumber")
                .build();
    }
}
