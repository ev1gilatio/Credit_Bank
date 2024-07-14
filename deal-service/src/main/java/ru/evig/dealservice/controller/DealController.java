package ru.evig.dealservice.controller;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.entity.Client;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.enums.ApplicationStatus;
import ru.evig.dealservice.enums.EmailTheme;
import ru.evig.dealservice.service.DealService;
import ru.evig.dealservice.service.KafkaService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {
    private final DealClient dealClient;
    private final DealService service;
    private final KafkaService kafka;

    @Operation(summary = "Валидация и список возможных предложений",
            description = "Производится валидация входных данных, " +
                    "отправляется запрос в calculator-service, " +
                    "из calculator-service возвращается список из 4-х предложений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная валидация и обращение в calculator-service"),
            @ApiResponse(responseCode = "400",description = "Какие-то данные не прошли валидацию " +
                    "или прескоринг в calculator-service")
    })
    @PostMapping("/statement")
    private List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto) {
        List<LoanOfferDto> dealClientResponseList = dealClient.getLoanOfferDtoList(lsrDto);
        Client client = service.createClient(lsrDto);
        Statement statement = service.createStatement(client);

        return service.getLoanOfferDtoList(dealClientResponseList, statement.getId());
    }

    @Operation(summary = "Выбор определенного предложения",
            description = "Клиент выбирает одно из предложений, " +
                    "предложение сохраняется в statement, " +
                    "клиенту отправляется email с запросом на завершение регистрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Все данные выбранного LoanOfferDto верны"),
            @ApiResponse(responseCode = "400", description = "Какие-то данные в LoanOfferDto неверны")
    })
    @PostMapping("/offer/select")
    private void selectLoanOffer(@Valid @RequestBody LoanOfferDto loDto) {
        service.checkDeniedStatus(loDto.getStatementId().toString());
        service.selectLoanOffer(loDto);

        sendMessageToTopic(loDto.getStatementId().toString(),
                EmailTheme.FINISH_REGISTRATION,
                "finish-registration"
        );
    }

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
    private void createCredit(@Valid @RequestBody FinishRegistrationRequestDto frrDto,
                              @PathVariable String statementId) {
        ScoringDataDto sdDto = service.getScoringDataDto(frrDto, statementId);

        try {
            CreditDto dealClientCreditDto = dealClient.getCreditDto(sdDto);

            service.completeBuildingClient(frrDto, statementId);
            service.createCredit(dealClientCreditDto, statementId);

            service.changeStatementStatus(statementId, ApplicationStatus.CC_APPROVED);
        } catch (FeignException.BadRequest e) {
            service.changeStatementStatus(statementId, ApplicationStatus.CC_DENIED);

            throw new RuntimeException(e.contentUTF8());
        }

        sendMessageToTopic(statementId,
                EmailTheme.SEND_DOCUMENTS,
                "send-documents"
        );
    }

    @Operation(summary = "Создание кредитных документов",
            description = "dossier-service обращается к deal-service и получает кредитные документы, " +
                    "клиенту отправляется email с кредитными документами и ссылкой на подписание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное изменение статусов в Statement"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/send")
    private void requestForSendingDocuments(@PathVariable String statementId) {
        service.changeStatementStatus(statementId, ApplicationStatus.PREPARE_DOCUMENTS);

        sendMessageToTopic(statementId,
                EmailTheme.CREATE_DOCUMENTS,
                "create-documents"
        );

        service.changeStatementStatus(statementId, ApplicationStatus.DOCUMENT_CREATED);
    }

    @Operation(summary = "Подписании кредитных документов",
            description = "Генерируется ses-code и сохраняется в Statement, " +
                    "dossier-service обращается к deal-service и получает ses-code, " +
                    "клиенту отправляется email с ses-code и ссылкой на завершение операции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная генерация ses-code"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/sign")
    private void requestForSigningDocuments(@PathVariable String statementId) {
        service.checkDeniedStatus(statementId);
        service.generateSesCode(statementId);

        sendMessageToTopic(statementId,
                EmailTheme.SEND_SES,
                "send-ses"
        );
    }

    @Operation(summary = "Завершение операции",
            description = "После перехода клиентом по этой ссылке, в БД обновляются все необходимые статусы, " +
                    "клиенту отправляется email с оповещением об успешном получении кредита")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное оформление кредита"),
            @ApiResponse(responseCode = "400", description = "Скорее всего передан неверный statementId")
    })
    @PostMapping("/document/{statementId}/code")
    private void signingDocuments(@PathVariable String statementId) {
        service.changeStatementStatus(statementId, ApplicationStatus.DOCUMENT_SIGNED);
        service.signDocuments(statementId);
        service.changeStatementStatus(statementId, ApplicationStatus.CREDIT_ISSUED);
        service.changeCreditStatus(statementId);

        sendMessageToTopic(statementId,
                EmailTheme.CREDIT_ISSUED,
                "credit-issued"
        );
    }

//    @Operation(summary = "Получение кредитных документов",
//            description = "Служебный эндпоинт для получения кредитных документов dossier-service'ом, " +
//                    "напрямую сюда обращаться не нужно")
    @GetMapping("/document/{statementId}/documentation")
    private String getDocumentation(@PathVariable String statementId) {

        return service.createDocumentation(statementId);
    }

//    @Operation(summary = "Получение ses-code",
//            description = "Служебный эндпоинт для получения ses-code dossier-service'ом" +
//                    "напрямую сюда обращаться не нужно")
    @GetMapping("/document/{statementId}/ses-code")
    private String getSesCode(@PathVariable String statementId) {

        return service.generateSesCode(statementId);
    }

    @Operation(summary = "Отмена операции",
            description = "Эндпоинт, на который переходит клиент, " +
                    "если захочет отменить процедуру кредитования")
    @GetMapping("/cancel/{statementId}")
    private void clientDenied(@PathVariable String statementId) {
        service.checkDeniedStatus(statementId);
        service.changeStatementStatus(statementId, ApplicationStatus.CLIENT_DENIED);

        sendMessageToTopic(statementId,
                EmailTheme.STATEMENT_DENIED,
                "statement-denied"
        );
    }

    private void sendMessageToTopic(String statementId, EmailTheme theme, String topic) {
        UUID id = UUID.fromString(statementId);
        Statement statement = service.findStatementInDB(id);

        EmailMessage message = EmailMessage.builder()
                .address(statement.getClientId().getEmail())
                .theme(theme)
                .statementId(id)
                .build();

        kafka.sendMessageToTopic(topic, message);
    }
}
