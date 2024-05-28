package ru.evig.calculatorservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.evig.calculatorservice.Exception.LoanRejectedException;
import ru.evig.calculatorservice.Exception.TooYoungForCreditException;
import ru.evig.calculatorservice.dto.*;
import ru.evig.calculatorservice.enums.EmploymentPosition;
import ru.evig.calculatorservice.enums.EmploymentStatus;
import ru.evig.calculatorservice.enums.Gender;
import ru.evig.calculatorservice.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CalculatorServiceTest {

    @Autowired
    CalculatorService service;

    @Test
    void shouldReturnTooYoungForCreditException() {
        TooYoungForCreditException thrown = assertThrows(
                TooYoungForCreditException.class,
                () -> service.getLoanOfferDtoList(getLsrDtoWithDifferentYear(2020))
        );

        assertTrue(thrown.getMessage().contains("The person is younger than 18"));
    }

    @Test
    void shouldReturnLoanOfferDtoList() {
        List<LoanOfferDto> expected = service.getLoanOfferDtoList(getLsrDtoWithDifferentYear(2002));
        BigDecimal amount = new BigDecimal(30000);
        BigDecimal totalAmountIsInsuranceEnabledFalse = new BigDecimal(30000);
        BigDecimal totalAmountIsInsuranceEnabledTrue = new BigDecimal(130000);
        BigDecimal rateIeeTrueIscTrue = new BigDecimal("6.5");
        BigDecimal rateIeeTrueIscFalse = new BigDecimal("7.5");
        BigDecimal rateIeeFalseIscTrue = new BigDecimal("9.5");
        BigDecimal rateIeeFalseIscFalse = new BigDecimal("10.5");

        assertEquals(expected.get(0).getRequestedAmount(), amount);
        assertEquals(totalAmountIsInsuranceEnabledTrue, expected.get(0).getTotalAmount());
        assertEquals(totalAmountIsInsuranceEnabledTrue, expected.get(1).getTotalAmount());
        assertEquals(totalAmountIsInsuranceEnabledFalse, expected.get(2).getTotalAmount());
        assertEquals(totalAmountIsInsuranceEnabledFalse, expected.get(3).getTotalAmount());
        assertEquals(rateIeeTrueIscTrue, expected.get(0).getRate());
        assertEquals(rateIeeTrueIscFalse, expected.get(1).getRate());
        assertEquals(rateIeeFalseIscTrue, expected.get(2).getRate());
        assertEquals(rateIeeFalseIscFalse, expected.get(3).getRate());
    }

    @Test
    void shouldReturnLoanRejectedExceptionBecauseOfUnemployed() {
        LoanRejectedException thrown = assertThrows(
                LoanRejectedException.class,
                () -> service.getCreditDto(getSdDto(
                        EmploymentStatus.UNEMPLOYED,
                        new BigDecimal(100000),
                        2002,
                        24,
                        12))
        );

        assertTrue(thrown.getMessage().contains("Person is unemployed"));
    }

    @Test
    void shouldReturnLoanRejectedExceptionBecauseOfLoanIsMoreThan25Salaries() {
        LoanRejectedException thrown = assertThrows(
                LoanRejectedException.class,
                () -> service.getCreditDto(getSdDto(
                        EmploymentStatus.EMPLOYEE,
                        new BigDecimal(100),
                        2002,
                        24,
                        12))
        );

        assertTrue(thrown.getMessage().contains("Loan amount is more than 25 salaries"));
    }

    @Test
    void shouldReturnLoanRejectedExceptionBecauseOfOld() {
        LoanRejectedException thrown = assertThrows(
                LoanRejectedException.class,
                () -> service.getCreditDto(getSdDto(
                        EmploymentStatus.EMPLOYEE,
                        new BigDecimal(100000),
                        1900,
                        24,
                        12))
        );

        assertTrue(thrown.getMessage().contains("The person is younger than 20 or older than 65"));
    }

    @Test
    void shouldReturnLoanRejectedExceptionBecauseOfYoung() {
        LoanRejectedException thrown = assertThrows(
                LoanRejectedException.class,
                () -> service.getCreditDto(getSdDto(
                        EmploymentStatus.EMPLOYEE,
                        new BigDecimal(100000),
                        2020,
                        24,
                        12))
        );

        assertTrue(thrown.getMessage().contains("The person is younger than 20 or older than 65"));
    }

    @Test
    void shouldReturnLoanRejectedExceptionBecauseOfTotalWorkExp() {
        LoanRejectedException thrown = assertThrows(
                LoanRejectedException.class,
                () -> service.getCreditDto(getSdDto(
                        EmploymentStatus.EMPLOYEE,
                        new BigDecimal(100000),
                        2002,
                        12,
                        12))
        );

        assertTrue(thrown.getMessage().contains("The person has not enough working time"));
    }

    @Test
    void shouldReturnLoanRejectedExceptionBecauseOfCurrentWorkExp() {
        LoanRejectedException thrown = assertThrows(
                LoanRejectedException.class,
                () -> service.getCreditDto(getSdDto(
                        EmploymentStatus.EMPLOYEE,
                        new BigDecimal(100000),
                        2002,
                        24,
                        2))
        );

        assertTrue(thrown.getMessage().contains("The person has not enough working time"));
    }

    @Test
    void shouldReturnCreditDto() {
        ScoringDataDto sdDto = getSdDto(EmploymentStatus.EMPLOYEE,
                new BigDecimal(100000),
                2002,
                24,
                12);
        CreditDto cDto = service.getCreditDto(sdDto);

        assertNotNull(cDto);
        assertEquals(new BigDecimal(30000), sdDto.getAmount());
        assertEquals(sdDto.getAmount(), cDto.getAmount());
        assertEquals(new BigDecimal("5109.94"), cDto.getMonthlyPayment());
        assertEquals(new BigDecimal("7.5"), cDto.getRate());
        assertEquals(cDto.getTerm(), cDto.getPaymentSchedule().size());
        assertEquals(cDto.getMonthlyPayment(), cDto.getPaymentSchedule().get(0).getTotalPayment());
    }

    private LoanStatementRequestDto getLsrDtoWithDifferentYear(int birthYear) {
        LoanStatementRequestDto lsrDto = new LoanStatementRequestDto();

        lsrDto.setAmount(new BigDecimal(30000));
        lsrDto.setTerm(6);
        lsrDto.setFirstName("Vladimir");
        lsrDto.setLastName("Vladimir");
        lsrDto.setEmail("vov@vov.vov");
        lsrDto.setBirthday(LocalDate.of(birthYear, 4, 27));
        lsrDto.setPassportSeries("0123");
        lsrDto.setPassportNumber("456789");

        return lsrDto;
    }

    private ScoringDataDto getSdDto(EmploymentStatus status,
                                    BigDecimal salary,
                                    int birthYear,
                                    int workExpTotal,
                                    int workExpCurrent) {
        EmploymentDto eDto = new EmploymentDto();
        ScoringDataDto sdDto = new ScoringDataDto();

        eDto.setEmploymentStatus(status);
        eDto.setEmploymentINN("INN");
        eDto.setSalary(salary);
        eDto.setPosition(EmploymentPosition.MILORD);
        eDto.setWorkExperienceTotal(workExpTotal);
        eDto.setWorkExperienceCurrent(workExpCurrent);

        sdDto.setAmount(new BigDecimal(30000));
        sdDto.setTerm(6);
        sdDto.setFirstName("Vladimir");
        sdDto.setLastName("Vladimir");
        sdDto.setGender(Gender.MALE);
        sdDto.setBirthday(LocalDate.of(birthYear, 4, 27));
        sdDto.setPassportSeries("0123");
        sdDto.setPassportNumber("456789");
        sdDto.setPassportIssueDate(LocalDate.of(2020, 4, 27));
        sdDto.setPassportIssueBranch("SBp NeoFlex");
        sdDto.setMaritalStatus(MaritalStatus.NOT_MARRIED);
        sdDto.setEmployment(eDto);
        sdDto.setAccountNumber("AccNumber");
        sdDto.setIsInsuranceEnabled(false);
        sdDto.setIsSalaryClient(false);

        return sdDto;
    }
}
