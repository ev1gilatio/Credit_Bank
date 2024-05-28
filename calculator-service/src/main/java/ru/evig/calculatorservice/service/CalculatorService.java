package ru.evig.calculatorservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.evig.calculatorservice.Exception.LoanRejectedException;
import ru.evig.calculatorservice.Exception.TooYoungForCreditException;
import ru.evig.calculatorservice.dto.*;
import ru.evig.calculatorservice.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculatorService {

    @Value("${loanBaseRate}")
    private BigDecimal loanBaseRate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<LoanOfferDto> getLoanOfferDtoList(LoanStatementRequestDto lsrDto) {
        log.info("Input data for getLoanOfferDto = " + lsrDto);

        if (LocalDate.from(lsrDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) < 18) {
            throw new TooYoungForCreditException("The person is younger than 18");
        }

        List<LoanOfferDto> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                BigDecimal isInsuranceLoanAdd = new BigDecimal(100000 * (i % 2));
                BigDecimal totalAmount = lsrDto.getAmount().add(isInsuranceLoanAdd);
                BigDecimal isInsuranceRateAdd = new BigDecimal(3 * (i % 2));
                BigDecimal isSalaryClientRateAdd = new BigDecimal(j % 2);
                BigDecimal totalRate = loanBaseRate.subtract(isInsuranceRateAdd).subtract(isSalaryClientRateAdd);
                BigDecimal totalMonthlyPayment = getMonthlyPayment(totalRate, totalAmount, lsrDto.getTerm());

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

                log.info("list[" + (list.size() - 1) + "] in getLoanOfferDto = " + loDto);
            }
        }

        Comparator<LoanOfferDto> compareByRate = Comparator
                .comparing(LoanOfferDto::getRate);

        log.info("Output data from getLoanOfferDto = " + list);

        return list.stream()
                .sorted(compareByRate)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public CreditDto getCreditDto(ScoringDataDto sdDto) {
        log.info("Input data for getCreditDto = " + sdDto);

        if (sdDto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new LoanRejectedException("Person is unemployed");
        } else if (!(sdDto.getAmount().compareTo(sdDto.getEmployment().getSalary().multiply(new BigDecimal(25))) < 0)) {
            throw new LoanRejectedException("Loan amount is more than 25 salaries");
        } else if (LocalDate.from(sdDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) < 20
                || LocalDate.from(sdDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) > 65) {
            throw new LoanRejectedException("The person is younger than 20 or older than 65");
        } else if (sdDto.getEmployment().getWorkExperienceTotal() < 18
                || sdDto.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new LoanRejectedException("The person has not enough working time");
        }

        BigDecimal totalAmount = sdDto.getAmount()
                .add(new BigDecimal(100000L * sdDto.getIsInsuranceEnabled().compareTo(false)));
        BigDecimal totalRate = loanBaseRate
                .add(sdDto.getEmployment().getEmploymentStatus().getRateAdd())
                .add(sdDto.getEmployment().getPosition().getRateAdd())
                .add(sdDto.getMaritalStatus().getRateAdd())
                .subtract(new BigDecimal(3 * sdDto.getIsInsuranceEnabled().compareTo(false)))
                .subtract(new BigDecimal(sdDto.getIsSalaryClient().compareTo(false)));
        BigDecimal totalMonthlyPayment = getMonthlyPayment(totalRate, totalAmount, sdDto.getTerm());
        BigDecimal totalPsk = totalMonthlyPayment.multiply(new BigDecimal(sdDto.getTerm()));

        CreditDto cDto = CreditDto.builder()
                .amount(totalAmount)
                .term(sdDto.getTerm())
                .monthlyPayment(totalMonthlyPayment)
                .rate(totalRate)
                .psk(totalPsk)
                .isInsuranceEnabled(sdDto.getIsInsuranceEnabled())
                .isSalaryClient(sdDto.getIsSalaryClient())
                .paymentSchedule(Collections.emptyList())
                .build();
        cDto.setPaymentSchedule(getPaymentScheduleList(cDto));

        log.info("Output data from getCreditDto = " + cDto);

        return cDto;
    }

    private List<PaymentScheduleElementDto> getPaymentScheduleList(CreditDto cDto) {
        log.info("Input data for getPaymentScheduleList = " + cDto);

        List<PaymentScheduleElementDto> list = new ArrayList<>();

        BigDecimal amount = cDto.getAmount();
        for (int i = 1; i < cDto.getTerm() + 1; i++) {
            BigDecimal totalInterestPayment = amount
                    .multiply(cDto.getRate())
                    .divide(new BigDecimal(1200), 20, RoundingMode.HALF_UP);
            BigDecimal totalDebtPayment = cDto.getMonthlyPayment().subtract(totalInterestPayment);
            BigDecimal totalRemainingDebt = amount.subtract(totalDebtPayment);

            PaymentScheduleElementDto pseDto = PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(cDto.getMonthlyPayment())
                    .interestPayment(totalInterestPayment)
                    .debtPayment(totalDebtPayment)
                    .remainingDebt(totalRemainingDebt)
                    .build();

            amount = amount.subtract(pseDto.getDebtPayment());

            doRounding(cDto, pseDto);

            list.add(pseDto);

            log.info("list[" + (i - 1) + "] in getPaymentScheduleList = " + list.get(i - 1));
        }

        log.info("Output data from getPaymentScheduleList = " + list);

        doRounding(cDto);

        return list;
    }

    private BigDecimal getMonthlyPayment(BigDecimal totalRate, BigDecimal totalAmount, Integer term) {
        BigDecimal monthlyRate = totalRate.divide(new BigDecimal(1200), 20, RoundingMode.HALF_UP);
        BigDecimal numerator = totalAmount
                .multiply(monthlyRate)
                .multiply((BigDecimal.ONE.add(monthlyRate)).pow(term));
        BigDecimal denominator = ((BigDecimal.ONE.add(monthlyRate)).pow(term))
                .subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 20, RoundingMode.HALF_UP);
    }

    private void doRounding(CreditDto cDto, PaymentScheduleElementDto pseDto) {
        pseDto.setTotalPayment(cDto.getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        pseDto.setInterestPayment(pseDto.getInterestPayment().setScale(2, RoundingMode.HALF_UP));
        pseDto.setDebtPayment(pseDto.getDebtPayment().setScale(2, RoundingMode.HALF_UP));
        pseDto.setRemainingDebt(pseDto.getRemainingDebt().setScale(2, RoundingMode.HALF_UP));
    }

    private void doRounding(CreditDto cDto) {
        cDto.setMonthlyPayment(cDto.getMonthlyPayment().setScale(2, RoundingMode.HALF_UP));
        cDto.setPsk(cDto.getPsk().setScale(2, RoundingMode.HALF_UP));
    }
}
