package ru.evig.calculatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.evig.calculatorservice.enums.Gender;
import ru.evig.calculatorservice.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ScoringDataDto {

    @NotNull
    @Min(30000)
    @Schema(example = "30000")
    private BigDecimal amount;

    @NotNull
    @Min(6)
    @Schema(example = "6")
    private Integer term;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    @Schema(example = "Vladimir")
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    @Schema(example = "Petrov")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    @Schema(example = "Jovanovich")
    private String middleName;

    @NotNull
    @Schema(example = "MALE")
    private Gender gender;

    @NotNull
    @Past
    @Schema(example = "2000-01-01")
    private LocalDate birthday;

    @NotNull
    @Pattern(regexp = "^[0-9]{4}$")
    @Schema(example = "0123")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    @Schema(example = "456789")
    private String passportNumber;

    @NotNull
    @Past
    @Schema(example = "2020-01-01")
    private LocalDate passportIssueDate;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]{1,256}$")
    @Schema(example = "NeoFlex SPb")
    private String passportIssueBranch;

    @NotNull
    @Schema(example = "NOT_MARRIED")
    private MaritalStatus maritalStatus;

    @Nullable
    @Schema(example = "0")
    private Integer dependentAmount;

    @Valid
    @NotNull
    private EmploymentDto employment;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{2,30}$")
    @Schema(example = "AccNumber")
    private String accountNumber;

    @NotNull
    @Schema(example = "false")
    private Boolean isInsuranceEnabled;

    @NotNull
    @Schema(example = "false")
    private Boolean isSalaryClient;
}
