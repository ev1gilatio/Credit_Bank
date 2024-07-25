package ru.evig.dealservice.dto;

import lombok.Builder;
import lombok.Data;
import ru.evig.dealservice.enums.EmailTheme;

import java.util.UUID;

@Data
@Builder
public class EmailMessage {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
}
