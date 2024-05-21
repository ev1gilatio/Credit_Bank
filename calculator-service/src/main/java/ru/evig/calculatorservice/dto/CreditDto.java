package ru.evig.calculatorservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditDto {
    private Enum gender;
    private Enum maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDto employment;
    private String accountNumber;
}
