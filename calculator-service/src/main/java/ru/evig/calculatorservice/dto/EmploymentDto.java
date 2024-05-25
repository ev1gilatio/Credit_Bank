package ru.evig.calculatorservice.dto;

import lombok.Data;
import ru.evig.calculatorservice.enums.EmploymentPosition;
import ru.evig.calculatorservice.enums.EmploymentStatus;

import java.math.BigDecimal;

@Data
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employmentINN;
    private BigDecimal salary;
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
