package ru.evig.dealservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class PassportDto {
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
