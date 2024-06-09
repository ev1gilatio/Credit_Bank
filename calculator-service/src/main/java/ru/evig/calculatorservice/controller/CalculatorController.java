package ru.evig.calculatorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.evig.calculatorservice.dto.CreditDto;
import ru.evig.calculatorservice.dto.LoanOfferDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;
import ru.evig.calculatorservice.dto.ScoringDataDto;
import ru.evig.calculatorservice.service.CalculatorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {
    private final CalculatorService service;

    @Operation(summary = "Прескоринг и список возможных предложений",
            description = "Производится прексоринг на основе полученной loanStatementRequestDto и возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный прескоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для прескоринга неудовлетворяют условиям")
    })
    @PostMapping("/offers")
    private List<LoanOfferDto> makeLoanOfferList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {

        return service.getLoanOfferDtoList(loanStatementRequestDto);
    }

    @Operation(summary = "Скоринг и расчет всех кредитных параметров",
            description = "Производится скоринг на основе полученной scoringDataDto и расчет всех кредитных параметров")
    @PostMapping("/calc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный скоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для скоринга неудовлетворяют условиям")
    })
    private CreditDto calculateCreditTerms(@Valid @RequestBody ScoringDataDto scoringDataDto) {

        return service.getCreditDto(scoringDataDto);
    }
}
