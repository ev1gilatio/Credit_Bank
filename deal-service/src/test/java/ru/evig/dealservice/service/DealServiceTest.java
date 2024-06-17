package ru.evig.dealservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;
import ru.evig.dealservice.exception.DtoNotFoundException;
import ru.evig.dealservice.exception.TooYoungForCreditException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DealServiceTest {

    @Autowired
    private DealService service;

    @Test
    void shouldReturnDtoNotFoundException() {
        DtoNotFoundException thrown = assertThrows(
                DtoNotFoundException.class,
                () -> service.selectLoanOffer(getLoDto())
        );

        assertTrue(thrown.getMessage().contains("Statement with ID 9d40f48e-213c-42ee-b468-2cab5b518f43 not found"));
    }

    @Test
    void shouldReturnTooYoungForCreditException() {
        TooYoungForCreditException thrown = assertThrows(
                TooYoungForCreditException.class,
                () -> service.createClient(getLsrDtoWithDifferentYear(2020))
        );

        assertTrue(thrown.getMessage().contains("The person is younger than 18"));
    }

    private LoanOfferDto getLoDto() {
        return LoanOfferDto.builder()
                .statementId(UUID.fromString("9d40f48e-213c-42ee-b468-2cab5b518f43"))
                .requestedAmount(new BigDecimal(30000))
                .totalAmount(new BigDecimal(30000))
                .term(6)
                .monthlyPayment(new BigDecimal("5154.24"))
                .rate(new BigDecimal("10.5"))
                .isInsuranceEnable(false)
                .isSalaryClient(false)
                .build();
    }

    private LoanStatementRequestDto getLsrDtoWithDifferentYear(int birthYear) {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName("Vladimir")
                .lastName("Vladimir")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(birthYear, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }
}
