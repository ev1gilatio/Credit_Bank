package ru.evig.dossierservice.dto;

import lombok.Data;
import ru.evig.dossierservice.enums.EmailTheme;

import java.util.UUID;

@Data
public class EmailMessage {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
}
