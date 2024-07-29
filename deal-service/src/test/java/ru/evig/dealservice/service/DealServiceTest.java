package ru.evig.dealservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.config.KafkaProducerConfig;
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

    @MockBean
    private KafkaService kafka;

    @MockBean
    private KafkaProducerConfig kafkaProducerConfig;

    @MockBean
    private DealClient dealClient;

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
        assertNotNull(statement.getClientId());
        assertEquals(client, statement.getClientId());
        assertEquals(ApplicationStatus.PREAPPROVAL, statement.getStatus());
        assertEquals(new ArrayList<>(), statement.getStatusHistory());
        assertNull(statement.getCreditId());
    }

    @Test
    void shouldReturnEntityNotFoundExceptionInsteadOfCredit() {
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> service.createCredit(getFrrDto(), "5d3ddd1d-eef2-414d-900f-a625669e5ad0")
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
