package ru.evig.calculatorservice.dto;

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
    private BigDecimal amount;

    @NotNull
    @Min(6)
    private Integer term;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String firstName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String middleName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    //гггг-мм-дд
    @NotNull
    @Past
    private LocalDate birthday;

    @NotNull
    @Pattern(regexp = "^[0-9]{4}$")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "^[0-9]{6}$")
    private String passportNumber;
}
