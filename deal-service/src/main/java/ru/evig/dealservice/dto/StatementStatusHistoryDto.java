package ru.evig.dealservice.dto;

import lombok.Data;
import ru.evig.dealservice.enums.ChangeType;
import ru.evig.dealservice.enums.StatementStatus;

import java.time.LocalDate;

@Data
public class StatementStatusHistoryDto {
    private StatementStatus status;
    private LocalDate time;
    private ChangeType changeType;
}
