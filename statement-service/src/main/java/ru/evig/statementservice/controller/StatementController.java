package ru.evig.statementservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.evig.statementservice.dto.LoanOfferDto;
import ru.evig.statementservice.dto.LoanStatementRequestDto;

import javax.validation.Valid;
import java.util.List;

public interface StatementController {

    @Operation(summary = "Прескоринг и список возможных предложений",
            description = "Производится прескоринг на основе полученной LoanStatementRequestDto, " +
                    "возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный прескоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для прескоринга неудовлетворяют условиям")
    })
    @PostMapping()
    List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto);

    @Operation(summary = "Выбор определенного предложения",
            description = "Клиент выбирает одно из предложений, " +
                    "преложение отправляется в deal-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все данные выбранного LoanOfferDto верны"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные в LoanOfferDto неверны")
    })
    @PostMapping("/offer")
    void selectLoanOffer(@Valid @RequestBody LoanOfferDto loDto);
}
