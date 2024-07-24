package ru.evig.dealservice.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.entity.Client;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.enums.ApplicationStatus;
import ru.evig.dealservice.service.DealService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealControllerImpl implements DealController {
    private final DealClient dealClient;
    private final DealService service;

    @Override
    @PostMapping("/statement")
    public List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto) {
        List<LoanOfferDto> dealClientResponseList = dealClient.getLoanOfferDtoList(lsrDto);
        Client client = service.createClient(lsrDto);
        Statement statement = service.createStatement(client);

        return service.getLoanOfferDtoList(dealClientResponseList, statement.getId());
    }

    @Override
    @PostMapping("/offer/select")
    public void selectLoanOffer(@Valid @RequestBody LoanOfferDto loDto) {
        service.selectLoanOffer(loDto);
    }

    @Override
    @PostMapping("/calculate/{statementId}")
    public void createCredit(@Valid @RequestBody FinishRegistrationRequestDto frrDto,
                              @PathVariable String statementId) {
        ScoringDataDto sdDto = service.getScoringDataDto(frrDto, statementId);

        try {
            CreditDto dealClientCreditDto = dealClient.getCreditDto(sdDto);
            service.createCredit(dealClientCreditDto, frrDto, statementId);
        } catch (FeignException.BadRequest e) {
            service.changeStatementStatus(statementId, ApplicationStatus.CC_DENIED);

            throw e;
        }
    }

    @Override
    @PostMapping("/document/{statementId}/send")
    public void requestForSendingDocuments(@PathVariable String statementId) {
        service.documentPerforming(statementId);
    }

    @Override
    @PostMapping("/document/{statementId}/sign")
    public void requestForSigningDocuments(@PathVariable String statementId) {
        service.sesCodePerforming(statementId);
    }

    @Override
    @PostMapping("/document/{statementId}/code")
    public void signingDocuments(@PathVariable String statementId) {
        service.finishOperation(statementId);
    }

    @Override
    @GetMapping("/document/{statementId}")
    public Statement getStatement(@PathVariable String statementId) {

        return service.findStatementInDB(UUID.fromString(statementId));
    }

    @Override
    @GetMapping("/document/{statementId}/ses-code")
    public String getSesCode(@PathVariable String statementId) {

        return service.generateSesCode(statementId);
    }

    @Override
    @GetMapping("/cancel/{statementId}")
    public void clientDenied(@PathVariable String statementId) {
        service.denialPerforming(statementId);
    }
}
