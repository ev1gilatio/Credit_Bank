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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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

        LoanOfferDto loDto;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                loDto = new LoanOfferDto();

                loDto.setStatementId(UUID.randomUUID());
                loDto.setIsInsuranceEnable(i % 2 != 0);
                loDto.setIsSalaryClient(j % 2 != 0);
                loDto.setRequestedAmount(lsrDto.getAmount());
                loDto.setTerm(lsrDto.getTerm());

                BigDecimal isInsuranceLoanAdd = new BigDecimal(100000 * (i % 2));
                BigDecimal totalAmount = lsrDto.getAmount().add(isInsuranceLoanAdd);
                loDto.setTotalAmount(totalAmount);

                BigDecimal isInsuranceRateAdd = new BigDecimal(3 * (i % 2));
                BigDecimal isSalaryClientRateAdd = new BigDecimal(j % 2);
                BigDecimal totalRate = loanBaseRate.subtract(isInsuranceRateAdd).subtract(isSalaryClientRateAdd);
                loDto.setRate(totalRate);

                BigDecimal totalMonthlyPayment = getMonthlyPayment(totalRate, totalAmount, lsrDto.getTerm());
                loDto.setMonthlyPayment(totalMonthlyPayment.setScale(2, RoundingMode.HALF_UP));

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

        CreditDto cDto = new CreditDto();

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

        cDto.setIsInsuranceEnabled(sdDto.getIsInsuranceEnabled());
        cDto.setIsSalaryClient(sdDto.getIsSalaryClient());
        cDto.setTerm(sdDto.getTerm());

        BigDecimal totalAmount = sdDto.getAmount()
                .add(new BigDecimal(100000L * sdDto.getIsInsuranceEnabled().compareTo(false)));
        cDto.setAmount(totalAmount);

        BigDecimal totalRate = loanBaseRate
                .add(sdDto.getEmployment().getEmploymentStatus().getRateAdd())
                .add(sdDto.getEmployment().getPosition().getRateAdd())
                .add(sdDto.getMaritalStatus().getRateAdd());
        cDto.setRate(totalRate);

        if (sdDto.getIsInsuranceEnabled()) {
            cDto.setRate(cDto.getRate().subtract(new BigDecimal(3)));
        }
        if (sdDto.getIsSalaryClient()) {
            cDto.setRate(cDto.getRate().subtract(BigDecimal.ONE));
        }

        BigDecimal totalMonthlyPayment = getMonthlyPayment(totalRate, totalAmount, sdDto.getTerm());
        cDto.setMonthlyPayment(totalMonthlyPayment);

        BigDecimal totalPsk = totalMonthlyPayment.multiply(new BigDecimal(sdDto.getTerm()));
        cDto.setPsk(totalPsk);
        cDto.setPaymentSchedule(getPaymentScheduleList(cDto));

        log.info("Output data from getCreditDto = " + cDto);

        return cDto;
    }

    private List<PaymentScheduleElementDto> getPaymentScheduleList(CreditDto cDto) {
        log.info("Input data for getPaymentScheduleList = " + cDto);

        List<PaymentScheduleElementDto> list = new ArrayList<>();
        PaymentScheduleElementDto pseDto;

        BigDecimal amount = cDto.getAmount();
        BigDecimal totalInterestPayment;
        BigDecimal totalDebtPayment;
        BigDecimal totalRemainingDebt;

        for (int i = 1; i < cDto.getTerm() + 1; i++) {
            pseDto = new PaymentScheduleElementDto();

            pseDto.setNumber(i);
            pseDto.setDate(LocalDate.now().plusMonths(i));
            pseDto.setTotalPayment(cDto.getMonthlyPayment());

            totalInterestPayment = amount
                    .multiply(cDto.getRate())
                    .divide(new BigDecimal(1200), 20, RoundingMode.HALF_UP);
            pseDto.setInterestPayment(totalInterestPayment);

            totalDebtPayment = cDto.getMonthlyPayment().subtract(totalInterestPayment);
            pseDto.setDebtPayment(totalDebtPayment);

            totalRemainingDebt = amount.subtract(totalDebtPayment);
            pseDto.setRemainingDebt(totalRemainingDebt);

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
