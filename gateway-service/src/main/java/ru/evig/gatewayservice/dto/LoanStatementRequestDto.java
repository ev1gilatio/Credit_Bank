package ru.evig.gatewayservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {

    @Schema(example = "30000")
    private BigDecimal amount;

    @Schema(example = "6")
    private Integer term;

    @Schema(example = "Vladimir")
    private String firstName;

    @Schema(example = "Petrov")
    private String lastName;

    @Schema(example = "Jovanovich")
    private String middleName;

    @Schema(example = "vov@vov.ru")
    private String email;

    @Schema(example = "2000-01-01")
    private LocalDate birthday;

    @Schema(example = "0123")
    private String passportSeries;

    @Schema(example = "456789")
    private String passportNumber;
}
