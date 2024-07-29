package ru.evig.dossierservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class Statement {
    private UUID id;
    private Client clientId;
    private Credit creditId;
}
