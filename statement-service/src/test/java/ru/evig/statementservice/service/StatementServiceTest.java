package ru.evig.statementservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.evig.statementservice.dto.LoanStatementRequestDto;
import ru.evig.statementservice.exception.TooYoungForCreditException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @InjectMocks
    private StatementService service;

    @Test
    void shouldReturnTooYoungForCreditException() {
        TooYoungForCreditException thrown = assertThrows(
                TooYoungForCreditException.class,
                () -> service.makePreScoring(getLsrDto())
        );

        assertTrue(thrown.getMessage().contains("The person is younger than 18"));
    }

    private LoanStatementRequestDto getLsrDto() {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName("Vladimir")
                .lastName("Petrov")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(2022, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }
}
