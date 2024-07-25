package ru.evig.gatewayservice.dto;

import lombok.Data;
import ru.evig.gatewayservice.enums.Gender;
import ru.evig.gatewayservice.enums.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Client {
    private UUID id;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private PassportDto passport;
    private EmploymentDto employment;
    private String accountNumber;
}
