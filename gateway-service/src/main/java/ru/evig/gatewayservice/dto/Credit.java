package ru.evig.gatewayservice.dto;

import lombok.Data;
import ru.evig.gatewayservice.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class Credit {
    private UUID id;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private List<PaymentScheduleElementDto> paymentSchedule;
    private boolean insuranceEnabled;
    private boolean salaryClient;
    private CreditStatus creditStatus;
}
