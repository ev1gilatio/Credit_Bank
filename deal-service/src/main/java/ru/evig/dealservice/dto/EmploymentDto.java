package ru.evig.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.evig.dealservice.enums.EmploymentPosition;
import ru.evig.dealservice.enums.EmploymentStatus;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto {

    @NotNull
    @Schema(example = "EMPLOYEE")
    private EmploymentStatus employmentStatus;

    @NotNull
    @Schema(example = "INN")
    private String employmentINN;

    @NotNull
    @Schema(example = "100000")
    private BigDecimal salary;

    @NotNull
    @Schema(example = "SENIOR")
    private EmploymentPosition position;

    @NotNull
    @Schema(example = "24")
    private Integer workExperienceTotal;

    @NotNull
    @Schema(example = "12")
    private Integer workExperienceCurrent;
}
