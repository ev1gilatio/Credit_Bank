package ru.evig.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.evig.dealservice.enums.Gender;
import ru.evig.dealservice.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
public class FinishRegistrationRequestDto {

    @NotNull
    @Schema(example = "MALE")
    private Gender gender;

    @NotNull
    @Schema(example = "NOT_MARRIED")
    private MaritalStatus maritalStatus;

    @Nullable
    @Schema(example = "0")
    private Integer dependentAmount;

    @NotNull
    @Past
    @Schema(example = "2020-01-01")
    private LocalDate passportIssueDate;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]{1,256}$")
    @Schema(example = "NeoFlex SPb")
    private String passportIssueBranch;

    @Valid
    @NotNull
    private EmploymentDto employment;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{2,30}$")
    @Schema(example = "AccNumber")
    private String accountNumber;
}
