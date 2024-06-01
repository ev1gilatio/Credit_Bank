package ru.evig.calculatorservice.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.evig.calculatorservice.enums.Gender;
import ru.evig.calculatorservice.enums.MaritalStatus;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ScoringDataDto {

    @NotNull
    @Min(30000)
    private BigDecimal amount;

    @NotNull
    @Min(6)
    private Integer term;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String middleName;

    @NotNull
    private Gender gender;

    //гггг-мм-дд
    @NotNull
    @Past
    private LocalDate birthday;

    @NotNull
    @Pattern(regexp = "^[0-9]{4}$")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    private String passportNumber;

    @NotNull
    @Past
    private LocalDate passportIssueDate;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]{1,256}$")
    private String passportIssueBranch;

    @NotNull
    private MaritalStatus maritalStatus;

    @Nullable
    private Integer dependentAmount;

    private EmploymentDto employment;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{2,30}$")
    private String accountNumber;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;
}
