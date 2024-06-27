package ru.evig.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.evig.dealservice.enums.EmploymentPosition;
import ru.evig.dealservice.enums.EmploymentStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto {

    @NotNull
    @Schema(example = "EMPLOYEE")
    private EmploymentStatus employmentStatus;

    @NotNull
    @Pattern(regexp = "^[0-9]{10,12}$")
    @Schema(example = "0123456789")
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
