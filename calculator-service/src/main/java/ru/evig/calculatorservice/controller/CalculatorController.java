package ru.evig.calculatorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.evig.calculatorservice.dto.CreditDto;
import ru.evig.calculatorservice.dto.LoanOfferDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;
import ru.evig.calculatorservice.dto.ScoringDataDto;

import javax.validation.Valid;
import java.util.List;

public interface CalculatorController {

    @Operation(summary = "Прескоринг и список возможных предложений",
            description = "Производится прескоринг на основе полученной LoanStatementRequestDto, " +
                    "возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный прескоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для прескоринга неудовлетворяют условиям")
    })
    @PostMapping("/offers")
    List<LoanOfferDto> makeLoanOfferDtoList(@Valid @RequestBody LoanStatementRequestDto lsrDto);

    @Operation(summary = "Скоринг и расчет всех кредитных параметров",
            description = "Производится скоринг на основе полученной ScoringDataDto, " +
                    "рассчитываются все кредитные параметры")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный скоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для скоринга неудовлетворяют условиям")
    })
    @PostMapping("/calc")
    CreditDto calculateCreditTerms(@Valid @RequestBody ScoringDataDto sdDto);
}
