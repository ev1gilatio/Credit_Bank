package ru.evig.gatewayservice.dto;

import lombok.Data;
import ru.evig.gatewayservice.enums.ApplicationStatus;
import ru.evig.gatewayservice.enums.ChangeType;

import java.time.LocalDateTime;

@Data
public class StatementStatusHistoryDto {
    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;
}
