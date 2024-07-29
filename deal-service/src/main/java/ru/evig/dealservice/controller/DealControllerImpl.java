package ru.evig.dealservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.service.DealService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealControllerImpl implements DealController {
    private final DealService service;

    @Override
    @PostMapping("/statement")
    public List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto) {

        return service.getLoanOfferDtoList(lsrDto);
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
        service.createCredit(frrDto, statementId);
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

    @Override
    @GetMapping("/admin/statement/{statementId}")
    public Statement getAdminStatement(@PathVariable String statementId) {

        return service.findStatementInDB(UUID.fromString(statementId));
    }

    @Override
    @GetMapping("/admin/statement")
    public List<Statement> getAdminAllStatements() {

        return service.findAllStatementsInDB();
    }
}
