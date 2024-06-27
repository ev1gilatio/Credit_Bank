package ru.evig.statementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class LoanOfferDto {

    @NotNull
    @Schema(example = "de1b74b7-f3fa-4ba5-9b96-63ca9db4ae9a")
    private UUID statementId;

    @NotNull
    @Min(30000)
    @Schema(example = "30000")
    private BigDecimal requestedAmount;

    @NotNull
    @Min(30000)
    @Schema(example = "30000")
    private BigDecimal totalAmount;

    @NotNull
    @Min(6)
    @Schema(example = "6")
    private Integer term;

    @NotNull
    @Positive
    @Schema(example = "5154.24")
    private BigDecimal monthlyPayment;

    @NotNull
    @Positive
    @Schema(example = "10.5")
    private BigDecimal rate;

    @NotNull
    @Schema(example = "false")
    private Boolean isInsuranceEnable;

    @NotNull
    @Schema(example = "false")
    private Boolean isSalaryClient;
}
