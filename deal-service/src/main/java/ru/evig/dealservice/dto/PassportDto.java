package ru.evig.dealservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class PassportDto {
    private UUID passportUUID;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
