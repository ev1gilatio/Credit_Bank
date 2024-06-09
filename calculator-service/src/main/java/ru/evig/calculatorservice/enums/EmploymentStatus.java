package ru.evig.calculatorservice.enums;

import java.math.BigDecimal;

public enum EmploymentStatus {
    UNEMPLOYED (new BigDecimal(666)),
    SELF_EMPLOYED (new BigDecimal(1)),
    BUSINESS_OWNER (new BigDecimal(21)),
    EMPLOYEE (new BigDecimal(0));

    private final BigDecimal rateAdd;

    EmploymentStatus(BigDecimal rateAdd) {
        this.rateAdd = rateAdd;
    }

    public BigDecimal getRateAdd() {
        return rateAdd;
    }
}
