package ru.evig.calculatorservice.enums;

import java.math.BigDecimal;

public enum EmploymentPosition {
    JUNIOR (new BigDecimal(-1)),
    MIDDLE (new BigDecimal(-2)),
    SENIOR (new BigDecimal(-3));

    private final BigDecimal rateAdd;

    EmploymentPosition(BigDecimal rateAdd) {
        this.rateAdd = rateAdd;
    }

    public BigDecimal getRateAdd() {
        return rateAdd;
    }
}
