package ru.evig.dossierservice.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Statement {
    private UUID id;
    private Client clientId;
    private Credit creditId;
}
