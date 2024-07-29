package ru.evig.gatewayservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.evig.gatewayservice.enums.Gender;
import ru.evig.gatewayservice.enums.MaritalStatus;

import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDto {

    @Schema(example = "MALE")
    private Gender gender;

    @Schema(example = "NOT_MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(example = "0")
    private Integer dependentAmount;

    @Schema(example = "2020-01-01")
    private LocalDate passportIssueDate;

    @Schema(example = "NeoFlex SPb")
    private String passportIssueBranch;

    private EmploymentDto employment;

    @Schema(example = "AccNumber")
    private String accountNumber;
}
