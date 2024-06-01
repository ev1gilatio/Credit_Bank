package ru.evig.calculatorservice.dto;

import lombok.Builder;
import lombok.Data;
import ru.evig.calculatorservice.enums.EmploymentPosition;
import ru.evig.calculatorservice.enums.EmploymentStatus;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto {

    @NotNull
    private EmploymentStatus employmentStatus;

    @NotNull
    private String employmentINN;

    @NotNull
    private BigDecimal salary;

    @NotNull
    private EmploymentPosition position;

    @NotNull
    private Integer workExperienceTotal;

    @NotNull
    private Integer workExperienceCurrent;
}
