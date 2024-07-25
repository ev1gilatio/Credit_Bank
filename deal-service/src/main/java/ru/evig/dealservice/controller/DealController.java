package ru.evig.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.evig.dealservice.dto.FinishRegistrationRequestDto;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;
import ru.evig.dealservice.entity.Statement;

import javax.validation.Valid;
import java.util.List;

public interface DealController {

    @Operation(summary = "Валидация и список возможных предложений",
            description = "Производится валидация входных данных, " +
                    "отправляется запрос в calculator-service, " +
                    "из calculator-service возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная валидация и обращение в calculator-service"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные не прошли валидацию " +
                    "или прескоринг в calculator-service")
    })
    @PostMapping("/statement")
    List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto);

    @Operation(summary = "Выбор определенного предложения",
            description = "Клиент выбирает одно из предложений, " +
                    "предложение сохраняется в statement, " +
                    "клиенту отправляется email с запросом на завершение регистрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все данные выбранного LoanOfferDto верны"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные в LoanOfferDto неверны")
    })
    @PostMapping("/offer/select")
    void selectLoanOffer(@Valid @RequestBody LoanOfferDto loDto);

    @Operation(summary = "Валидация и создание кредитной сущности",
            description = "Формируется ScoringDataDto, " +
                    "отправляется в calculator-service, " +
                    "на основе полученной CreditDto создается Credit и сохраняется в БД, " +
                    "клиенту отправляется email c ссылкой для получения кредитных документов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная валидация и обращение в calculator-service"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные не прошли валидацию, " +
                    "или скоринг в calculator-service")
    })
    @PostMapping("/calculate/{statementId}")
    void createCredit(@Valid @RequestBody FinishRegistrationRequestDto frrDto, @PathVariable String statementId);

    @Operation(summary = "Создание кредитных документов",
            description = "dossier-service обращается к deal-service и получает кредитные документы, " +
                    "клиенту отправляется email с кредитными документами и ссылкой на подписание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное изменение статусов в Statement"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/send")
    void requestForSendingDocuments(@PathVariable String statementId);

    @Operation(summary = "Подписании кредитных документов",
            description = "Генерируется ses-code и сохраняется в Statement, " +
                    "dossier-service обращается к deal-service и получает ses-code, " +
                    "клиенту отправляется email с ses-code и ссылкой на завершение операции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная генерация ses-code"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/sign")
    void requestForSigningDocuments(@PathVariable String statementId);

    @Operation(summary = "Завершение операции",
            description = "После перехода клиентом по этой ссылке, в БД обновляются все необходимые статусы, " +
                    "клиенту отправляется email с оповещением об успешном получении кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное оформление кредита"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/code")
    void signingDocuments(@PathVariable String statementId);

    @Operation(summary = "Получение сущности Statement",
            description = "Служебный эндпоинт для получения сущности Statement dossier-service'ом, " +
                    "напрямую сюда обращаться не нужно")
    @GetMapping("/document/{statementId}")
    Statement getStatement(@PathVariable String statementId);

    @Operation(summary = "Получение ses-code",
            description = "Служебный эндпоинт для получения ses-code dossier-service'ом" +
                    "напрямую сюда обращаться не нужно")
    @GetMapping("/document/{statementId}/ses-code")
    String getSesCode(@PathVariable String statementId);

    @Operation(summary = "Отмена операции",
            description = "Эндпоинт, на который переходит клиент, " +
                    "если захочет отменить процедуру кредитования")
    @GetMapping("/cancel/{statementId}")
    void clientDenied(@PathVariable String statementId);
}
