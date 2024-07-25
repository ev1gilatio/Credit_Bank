package ru.evig.dossierservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Credit {
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private boolean insuranceEnabled;
    private boolean salaryClient;
}
