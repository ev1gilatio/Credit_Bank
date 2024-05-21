package ru.evig.calculatorservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String middleName;
    private Enum gender;
    private LocalDate birthday;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private Enum maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnable;
    private Boolean isSalaryClient;
}
