package ru.evig.dossierservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDetails {
    private String to;
    private String subject;
    private String text;
}
