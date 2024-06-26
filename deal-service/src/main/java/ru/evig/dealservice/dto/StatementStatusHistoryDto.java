package ru.evig.dealservice.dto;

import lombok.Builder;
import lombok.Data;
import ru.evig.dealservice.enums.ApplicationStatus;
import ru.evig.dealservice.enums.ChangeType;

import java.time.LocalDateTime;

@Data
@Builder
public class StatementStatusHistoryDto {
    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;
}
