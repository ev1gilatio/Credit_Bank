package ru.evig.dossierservice.entity;

import lombok.Data;
import ru.evig.dossierservice.dto.PassportDto;

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
