package ru.evig.calculatorservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentDto {
    private Enum employmentStatus;
    private String employmentINN;
    private BigDecimal salary;
    private Enum position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
