package ru.evig.gatewayservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.evig.gatewayservice.enums.EmploymentPosition;
import ru.evig.gatewayservice.enums.EmploymentStatus;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto {

    @Schema(example = "EMPLOYEE")
    private EmploymentStatus employmentStatus;

    @Schema(example = "0123456789")
    private String employmentINN;

    @Schema(example = "100000")
    private BigDecimal salary;

    @Schema(example = "SENIOR")
    private EmploymentPosition position;

    @Schema(example = "24")
    private Integer workExperienceTotal;

    @Schema(example = "12")
    private Integer workExperienceCurrent;
}
