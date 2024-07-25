package ru.evig.gatewayservice.dto;

import lombok.Data;
import ru.evig.gatewayservice.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Statement {
    private UUID id;
    private Client clientId;
    private Credit creditId;
    private ApplicationStatus status;
    private LocalDateTime creationDate;
    private LoanOfferDto appliedOffer;
    private LocalDateTime signDate;
    private String sesCode;
    private List<StatementStatusHistoryDto> statusHistory;
}
