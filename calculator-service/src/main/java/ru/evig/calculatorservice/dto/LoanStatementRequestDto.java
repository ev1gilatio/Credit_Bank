package ru.evig.calculatorservice.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {

    @Min(30000)
    private BigDecimal amount;

    @Min(6)
    private Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String lastName;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z]{2,30}$")
    private String middleName;

    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    //гггг-мм-дд
    @Past
    private LocalDate birthday;

    @Pattern(regexp = "^[0-9]{4}$")
    private String passportSeries;

    @Pattern(regexp = "^[0-9]{6}$")
    private String passportNumber;
}
