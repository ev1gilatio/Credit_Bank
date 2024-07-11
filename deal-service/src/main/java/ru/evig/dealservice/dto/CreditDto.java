package ru.evig.dealservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreditDto {
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
    private List<PaymentScheduleElementDto> paymentSchedule;
}
