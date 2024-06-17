package ru.evig.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.dto.FinishRegistrationRequestDto;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;
import ru.evig.dealservice.service.DealService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {
    private final DealClient dealClient;
    private final DealService service;

    @Operation(summary = "Прескоринг и список возможных предложений",
            description = "Производится прексоринг на основе полученной loanStatementRequestDto, " +
                    "отправляется запрос в calculator-service на /calculator/offers " +
                    "и возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный прескоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для прескоринга неудовлетворяют условиям")
    })
    @PostMapping("/statement")
    private List<LoanOfferDto> makeLoanOfferList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        service.createClient(loanStatementRequestDto);

        return service.getLoanOfferDtoList(dealClient.getLoanOfferDtoList(loanStatementRequestDto));
    }

    @Operation(summary = "Выбор определенного предложения",
            description = "Выбранное предложение сохраняется в statement, statement сохраняется в БД")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное сохранение"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные в LoanOfferDto неверны")
    })
    @PostMapping("/offer/select")
    private void selectLoanOffer(@Valid @RequestBody LoanOfferDto loanOfferDto) {
        service.selectLoanOffer(loanOfferDto);
    }

    @Operation(summary = "Скоринг и сохранение в БД",
            description = "scoringDataDto насыщается всем, чем возможно, " +
                    "отправляется запрос в calculator-service на /calculator/calc " +
                    "и условия кредита сохраняются в statement, statement сохраняется в БД")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный скоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для скоринга неудовлетворяют условиям")
    })
    @PostMapping("/calculate/{statementId}")
    private void makeCredit(@Valid @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                            @PathVariable String statementId) {
        service.makeCredit(dealClient.getCreditDto(
                service.getScoringDataDto(finishRegistrationRequestDto, statementId))
        );
    }
}
