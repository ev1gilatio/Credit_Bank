package ru.evig.dealservice.controller;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.enums.EmploymentPosition;
import ru.evig.dealservice.enums.EmploymentStatus;
import ru.evig.dealservice.enums.Gender;
import ru.evig.dealservice.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DealControllerTest {

    @Autowired
    private DealClient dealClient;

    @Test
    void shouldReturnMethodArgumentNotValidExceptionInsteadOfLoanOfferDtoList() {
        FeignException thrown = assertThrows(
                FeignException.class,
                () -> dealClient.getLoanOfferDtoList(getLsrDto(null))
        );

        assertTrue(thrown.getMessage().contains(
                "[400] during [POST] to [http://localhost:8080/calculator/offers] " +
                        "[DealClient#getLoanOfferDtoList(LoanStatementRequestDto)]: " +
                        "[{\"firstName\":\"не должно равняться null\"}]"
        ));
    }

    @Test
    void shouldReturnLoanOfferDtoList() {
        List<LoanOfferDto> list = dealClient.getLoanOfferDtoList(getLsrDto("Vladimir"));

        BigDecimal amount = new BigDecimal(30000);
        BigDecimal totalAmount = new BigDecimal(30000);
        BigDecimal rate = new BigDecimal("10.5");

        assertNotNull(list);
        assertEquals(4, list.size());
        assertNull(list.get(0).getStatementId());
        assertEquals(6, list.get(0).getTerm());
        assertEquals(amount, list.get(0).getRequestedAmount());
        assertEquals(totalAmount, list.get(0).getTotalAmount());
        assertEquals(rate, list.get(0).getRate());
    }

    @Test
    void shouldReturnMethodArgumentNotValidExceptionInsteadOfCreditDto() {
        FeignException thrown = assertThrows(
                FeignException.class,
                () -> dealClient.getCreditDto(getSdDto(null))
        );

        assertTrue(thrown.getMessage().contains(
                "[400] during [POST] to [http://localhost:8080/calculator/calc] " +
                        "[DealClient#getCreditDto(ScoringDataDto)]: " +
                        "[{\"firstName\":\"не должно равняться null\"}]"
        ));
    }

    @Test
    void shouldReturnCreditDto() {
        CreditDto cDto = dealClient.getCreditDto(getSdDto("Vladimir"));

        assertNotNull(cDto);
        assertEquals(new BigDecimal(30000), cDto.getAmount());
        assertEquals(6, cDto.getTerm());
        assertEquals(new BigDecimal("7.5"), cDto.getRate());
        assertEquals(false, cDto.getIsInsuranceEnabled());
        assertEquals(false, cDto.getIsSalaryClient());
    }

    private LoanStatementRequestDto getLsrDto(String firstName) {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName(firstName)
                .lastName("Vladimir")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }

    private ScoringDataDto getSdDto(String firstName) {
        EmploymentDto eDto = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYEE)
                .employmentINN("INN")
                .salary(new BigDecimal(100000))
                .position(EmploymentPosition.SENIOR)
                .workExperienceTotal(24)
                .workExperienceCurrent(12)
                .build();

        return ScoringDataDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName(firstName)
                .lastName("Vladimir")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .passportIssueDate(LocalDate.of(2020, 4, 27))
                .passportIssueBranch("SBp NeoFlex")
                .maritalStatus(MaritalStatus.NOT_MARRIED)
                .employment(eDto)
                .accountNumber("AccNumber")
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
    }
}
