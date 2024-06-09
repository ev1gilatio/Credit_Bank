package ru.evig.calculatorservice.enums;

import java.math.BigDecimal;

public enum MaritalStatus {
    MARRIED(new BigDecimal(-3)),
    DIVORCED(new BigDecimal(1)),
    NOT_MARRIED(new BigDecimal(0));

    private final BigDecimal rateAdd;

    MaritalStatus(BigDecimal rateAdd) {
        this.rateAdd = rateAdd;
    }

    public BigDecimal getRateAdd() {
        return rateAdd;
    }
}
