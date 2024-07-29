package ru.evig.dossierservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Client {
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private PassportDto passport;
}
