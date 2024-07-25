package ru.evig.gatewayservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PassportDto {
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
