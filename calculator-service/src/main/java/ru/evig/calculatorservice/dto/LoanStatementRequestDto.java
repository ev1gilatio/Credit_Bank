package ru.evig.calculatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanStatementRequestDto {

    @NotNull
    @Min(30000)
    @Schema(example = "30000")
    private BigDecimal amount;

    @NotNull
    @Min(6)
    @Schema(example = "6")
    private Integer term;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    @Schema(example = "Vladimir")
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    @Schema(example = "Petrov")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    @Schema(example = "Jovanovich")
    private String middleName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @Schema(example = "vov@vov.ru")
    private String email;

    @NotNull
    @Past
    @Schema(example = "2000-01-01")
    private LocalDate birthday;

    @NotNull
    @Pattern(regexp = "^[0-9]{4}$")
    @Schema(example = "0123")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    @Schema(example = "456789")
    private String passportNumber;
}
