package ru.evig.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.evig.gatewayservice.dto.FinishRegistrationRequestDto;
import ru.evig.gatewayservice.dto.LoanOfferDto;
import ru.evig.gatewayservice.dto.LoanStatementRequestDto;
import ru.evig.gatewayservice.dto.Statement;

import java.util.List;

public interface GatewayController {

    @Operation(summary = "Список возможных предложений",
            description = "Отправляется запрос в statement-service, " +
                    "из statement-service возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обращение к statement-service"),
            @ApiResponse(responseCode = "400", description = "Неправильное заполнение json " +
                    "или провалившаяся валидация в следующих микросервисах")
    })
    @PostMapping("/offers")
    List<LoanOfferDto> getLoanOfferList(@RequestBody LoanStatementRequestDto lsrDto);

    @Operation(summary = "Выбор определенного предложения",
            description = "Клиент выбирает одно из предложений, " +
                    "предложение отправляется в statement-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обращение к statement-service"),
            @ApiResponse(responseCode = "400", description = "Неправильное заполнение json " +
                    "или провалившаяся валидация в следующих микросервисах")
    })
    @PostMapping("/offers/select")
    void selectLoanOffer(@RequestBody LoanOfferDto loDto);

    @Operation(summary = "Создание кредитной сущности",
            description = "FinishRegistrationRequestDto отправляется в deal-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обращение к deal-service"),
            @ApiResponse(responseCode = "400", description = "Неправильное заполнение json " +
                    "или провалившаяся валидация в следующих микросервисах")
    })
    @PostMapping("/create-credit/{statementId}")
    void createCredit(@RequestBody FinishRegistrationRequestDto frrDto,
                      @PathVariable String statementId);

    @Operation(summary = "Создание кредитных документов",
            description = "Запрос на отправку документов отправляется в deal-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обращение к deal-service"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/send")
    void requestForSendingDocuments(@PathVariable String statementId);

    @Operation(summary = "Подписании кредитных документов",
            description = "Запрос на подписание документов отправляется в deal-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обращение к deal-service"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/sign")
    void requestForSigningDocuments(@PathVariable String statementId);

    @Operation(summary = "Завершение операции",
            description = "Запрос на завершение операции отправляется в deal-service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обращение к deal-service"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/code")
    void signingDocuments(@PathVariable String statementId);

    @Operation(summary = "Отмена операции",
            description = "Эндпоинт, на который переходит клиент, " +
                    "если захочет отменить процедуру кредитования")
    @GetMapping("/cancel/{statementId}")
    void clientDenied(@PathVariable String statementId);

    @GetMapping("/admin/statement/{statementId}")
    Statement getAdminStatement(@PathVariable String statementId);

    @GetMapping("/admin/statement")
    List<Statement> getAdminAllStatements();
}
