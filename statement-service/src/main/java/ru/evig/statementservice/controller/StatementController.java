package ru.evig.statementservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evig.statementservice.StatementClient;
import ru.evig.statementservice.dto.LoanOfferDto;
import ru.evig.statementservice.dto.LoanStatementRequestDto;
import ru.evig.statementservice.service.StatementService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementController {
    private final StatementClient statementClient;
    private final StatementService service;

    @Operation(summary = "Прескоринг и список возможных предложений",
            description = "Производится прескоринг на основе полученной LoanStatementRequestDto, " +
                    "возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный прескоринг"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные для прескоринга неудовлетворяют условиям")
    })
    @PostMapping()
    private List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto) {
        service.makePreScoring(lsrDto);

        return statementClient.getLoanOfferDtoList(lsrDto);
    }

    @Operation(summary = "Выбор определенного предложения",
            description = "Клиент выбирает одно из предложений, " +
                    "преложение отправляется в deal-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все данные выбранного LoanOfferDto верны"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные в LoanOfferDto неверны")
    })
    @PostMapping("/offer")
    private void selectLoanOffer(@Valid @RequestBody LoanOfferDto loDto) {
        statementClient.selectLoanOffer(loDto);
    }
}
