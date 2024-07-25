package ru.evig.gatewayservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoanOfferDto {

    @Schema(example = "de1b74b7-f3fa-4ba5-9b96-63ca9db4ae9a")
    private UUID statementId;

    @Schema(example = "30000")
    private BigDecimal requestedAmount;

    @Schema(example = "30000")
    private BigDecimal totalAmount;

    @Schema(example = "6")
    private Integer term;

    @Schema(example = "5154.24")
    private BigDecimal monthlyPayment;

    @Schema(example = "10.5")
    private BigDecimal rate;

    @Schema(example = "false")
    private Boolean isInsuranceEnable;

    @Schema(example = "false")
    private Boolean isSalaryClient;
}
