package ru.evig.calculatorservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.evig.calculatorservice.dto.LoanOfferDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CalculatorService {
    //TODO сделать builder для loanOfferDto

    @Value("${loanBaseRate}")
    private BigDecimal loanBaseRateRate;

    public List<LoanOfferDto> getLoanOfferList(LoanStatementRequestDto lsrDto) {
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

                BigDecimal isInsuranceLoanAdd = BigDecimal.valueOf(100000 * (i % 2));
                loDto.setTotalAmount(loDto.getRequestedAmount().add(isInsuranceLoanAdd));

                BigDecimal isInsuranceRateAdd = BigDecimal.valueOf(-3 * (i % 2));
                BigDecimal isSalaryClientRateAdd = BigDecimal.valueOf(-1 * (j % 2));
                loDto.setRate(loanBaseRateRate.add(isInsuranceRateAdd).add(isSalaryClientRateAdd));

                BigDecimal monthlyRate = loDto.getRate().divide(BigDecimal.valueOf(12 * 100), 3, RoundingMode.HALF_UP);
                BigDecimal numerator = loDto.getTotalAmount()
                        .multiply(monthlyRate)
                        .multiply((BigDecimal.valueOf(1).add(monthlyRate)).pow(loDto.getTerm()));
                BigDecimal denominator = ((BigDecimal.valueOf(1).add(monthlyRate)).pow(loDto.getTerm()))
                        .subtract(BigDecimal.valueOf(1));
                loDto.setMonthlyPayment(numerator.divide(denominator, 3, RoundingMode.HALF_UP));

                list.add(loDto);
            }
        }

        return list;
    }
}
