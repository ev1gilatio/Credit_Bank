package ru.evig.calculatorservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.evig.calculatorservice.dto.*;
import ru.evig.calculatorservice.enums.EmploymentStatus;
import ru.evig.calculatorservice.exception.LoanRejectedException;
import ru.evig.calculatorservice.exception.TooYoungForCreditException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculatorService {

    @Value("${loanBaseRate}")
    private BigDecimal loanBaseRate;
    private final Integer minAge = 18;
    private final Integer maxAge = 65;
    private final Integer minTotalWorkExp = 18;
    private final Integer minCurrentWorkExp = 3;
    private final Integer insuranceLoanAdd = 100000;
    private final Integer insuranceRateAdd = 3;
    private final BigDecimal salaryMultiplier = new BigDecimal(25);

    public List<LoanOfferDto> getLoanOfferDtoList(LoanStatementRequestDto lsrDto) {
        log.info("Input data for getLoanOfferDto = " + lsrDto);

        return makeLoanOfferDtoList(lsrDto);
    }

    public CreditDto getCreditDto(ScoringDataDto sdDto) {
        log.info("Input data for getCreditDto = " + sdDto);

        return makeCreditDto(sdDto);
    }

    private List<LoanOfferDto> makeLoanOfferDtoList(LoanStatementRequestDto lsrDto) {
        if (LocalDate.from(lsrDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) < minAge) {
            throw new TooYoungForCreditException("The person is younger than 18");
        }

        List<LoanOfferDto> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                BigDecimal isInsuranceLoanAdd = new BigDecimal(insuranceLoanAdd * (i % 2));
                BigDecimal totalAmount = lsrDto.getAmount().add(isInsuranceLoanAdd);
                BigDecimal isInsuranceRateAdd = new BigDecimal(insuranceRateAdd * (i % 2));
                BigDecimal isSalaryClientRateAdd = new BigDecimal(j % 2);
                BigDecimal totalRate = loanBaseRate.subtract(isInsuranceRateAdd).subtract(isSalaryClientRateAdd);
                BigDecimal totalMonthlyPayment = calculateMonthlyPayment(totalRate, totalAmount, lsrDto.getTerm());

                LoanOfferDto loDto = LoanOfferDto.builder()
                        .statementId(UUID.randomUUID())
                        .requestedAmount(lsrDto.getAmount())
                        .totalAmount(totalAmount)
                        .term(lsrDto.getTerm())
                        .monthlyPayment(totalMonthlyPayment.setScale(2, RoundingMode.HALF_UP))
                        .rate(totalRate)
                        .isInsuranceEnable(i % 2 != 0)
                        .isSalaryClient(j % 2 != 0)
                        .build();

                list.add(loDto);

                log.info("list[" + (list.size() - 1) + "] in makeLoanOfferDtoList = " + loDto);
            }
        }

        Comparator<LoanOfferDto> compareByRate = Comparator
                .comparing(LoanOfferDto::getRate);

        log.info("Output data from makeLoanOfferDtoList = " + list);

        return list.stream()
                .sorted(compareByRate)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private CreditDto makeCreditDto(ScoringDataDto sdDto) {
        if (sdDto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new LoanRejectedException("Person is unemployed");
        } else if (!(sdDto.getAmount().compareTo(sdDto.getEmployment().getSalary().multiply(salaryMultiplier)) < 0)) {
            throw new LoanRejectedException("Loan amount is more than " + salaryMultiplier + " salaries");
        } else if (LocalDate.from(sdDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) < minAge
                || LocalDate.from(sdDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) > maxAge) {
            throw new LoanRejectedException("The person is younger than " + minAge + " or older than " + maxAge);
        } else if (sdDto.getEmployment().getWorkExperienceTotal() < minTotalWorkExp
                || sdDto.getEmployment().getWorkExperienceCurrent() < minCurrentWorkExp) {
            throw new LoanRejectedException("The person has not enough working time");
        }

        BigDecimal totalAmount = sdDto.getAmount()
                .add(new BigDecimal(insuranceLoanAdd * sdDto.getIsInsuranceEnabled().compareTo(false)));
        BigDecimal totalRate = loanBaseRate
                .add(sdDto.getEmployment().getEmploymentStatus().getRateAdd())
                .add(sdDto.getEmployment().getPosition().getRateAdd())
                .add(sdDto.getMaritalStatus().getRateAdd())
                .subtract(new BigDecimal(insuranceRateAdd * sdDto.getIsInsuranceEnabled().compareTo(false)))
                .subtract(new BigDecimal(sdDto.getIsSalaryClient().compareTo(false)));
        BigDecimal totalMonthlyPayment = calculateMonthlyPayment(totalRate, totalAmount, sdDto.getTerm());
        BigDecimal totalPsk = totalMonthlyPayment.multiply(new BigDecimal(sdDto.getTerm()));

        CreditDto cDto = CreditDto.builder()
                .amount(totalAmount)
                .term(sdDto.getTerm())
                .monthlyPayment(totalMonthlyPayment)
                .rate(totalRate)
                .psk(totalPsk)
                .isInsuranceEnabled(sdDto.getIsInsuranceEnabled())
                .isSalaryClient(sdDto.getIsSalaryClient())
                .paymentSchedule(makePaymentScheduleList(totalAmount, sdDto.getTerm(), totalRate, totalMonthlyPayment))
                .build();

        doRounding(cDto);

        log.info("Output data from makeCreditDto = " + cDto);

        return cDto;
    }

    private List<PaymentScheduleElementDto> makePaymentScheduleList(BigDecimal amount,
                                                                    Integer term,
                                                                    BigDecimal rate,
                                                                    BigDecimal monthlyPayment) {
        List<PaymentScheduleElementDto> list = new ArrayList<>();

        BigDecimal localAmount = amount;
        for (int i = 1; i < term + 1; i++) {
            BigDecimal totalInterestPayment = localAmount
                    .multiply(rate)
                    .divide(new BigDecimal(1200), 20, RoundingMode.HALF_UP);
            BigDecimal totalDebtPayment = monthlyPayment.subtract(totalInterestPayment);
            BigDecimal totalRemainingDebt = localAmount.subtract(totalDebtPayment);

            PaymentScheduleElementDto pseDto = PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(totalInterestPayment)
                    .debtPayment(totalDebtPayment)
                    .remainingDebt(totalRemainingDebt)
                    .build();

            localAmount = localAmount.subtract(pseDto.getDebtPayment());

            doRounding(pseDto);

            list.add(pseDto);

            log.info("list[" + (i - 1) + "] in getPaymentScheduleList = " + list.get(i - 1));
        }

        log.info("Output data from getPaymentScheduleList = " + list);

        return list;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal totalRate, BigDecimal totalAmount, Integer term) {
        BigDecimal monthlyRate = totalRate.divide(new BigDecimal(1200), 20, RoundingMode.HALF_UP);
        BigDecimal numerator = totalAmount
                .multiply(monthlyRate)
                .multiply((BigDecimal.ONE.add(monthlyRate)).pow(term));
        BigDecimal denominator = ((BigDecimal.ONE.add(monthlyRate)).pow(term))
                .subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 20, RoundingMode.HALF_UP);
    }

    private void doRounding(PaymentScheduleElementDto pseDto) {
        pseDto.setTotalPayment(pseDto.getTotalPayment().setScale(2, RoundingMode.HALF_UP));
        pseDto.setInterestPayment(pseDto.getInterestPayment().setScale(2, RoundingMode.HALF_UP));
        pseDto.setDebtPayment(pseDto.getDebtPayment().setScale(2, RoundingMode.HALF_UP));
        pseDto.setRemainingDebt(pseDto.getRemainingDebt().setScale(2, RoundingMode.HALF_UP));
    }

    private void doRounding(CreditDto cDto) {
        cDto.setMonthlyPayment(cDto.getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        cDto.setPsk(cDto.getPsk().setScale(2, RoundingMode.HALF_UP));
    }
}
