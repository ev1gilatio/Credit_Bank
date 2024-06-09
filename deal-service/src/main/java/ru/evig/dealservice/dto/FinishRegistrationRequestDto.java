package ru.evig.dealservice.dto;

import lombok.Data;
import ru.evig.dealservice.enums.Gender;
import ru.evig.dealservice.enums.MaritalStatus;

import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDto {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDto employment;
    private String accountNumber;
}
