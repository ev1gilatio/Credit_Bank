package ru.evig.calculatorservice.dto;

import lombok.Data;
import org.springframework.lang.Nullable;
import ru.evig.calculatorservice.enums.Gender;
import ru.evig.calculatorservice.enums.MaritalStatus;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDto {

    @Min(30000)
    private BigDecimal amount;

    @Min(6)
    private Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String middleName;

    @NotNull
    private Gender gender;

    //гггг-мм-дд
    @Past
    private LocalDate birthday;

    @Pattern(regexp = "^[0-9]{4}$")
    private String passportSeries;

    @Pattern(regexp = "^[0-9]{6}$")
    private String passportNumber;

    @Future
    private LocalDate passportIssueDate;

    //TODO придумать валидацию места выдачи паспорта
    private String passportIssueBranch;

    @NotNull
    private MaritalStatus maritalStatus;

    @Nullable
    private Integer dependentAmount;

    @NotNull
    private EmploymentDto employment;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{2,30}$")
    private String accountNumber;

    @NotNull
    private Boolean isInsuranceEnable;

    @NotNull
    private Boolean isSalaryClient;
}
