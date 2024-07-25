package ru.evig.gatewayservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.evig.gatewayservice.GatewayToDealClient;
import ru.evig.gatewayservice.GatewayToStatementClient;
import ru.evig.gatewayservice.dto.FinishRegistrationRequestDto;
import ru.evig.gatewayservice.dto.LoanOfferDto;
import ru.evig.gatewayservice.dto.LoanStatementRequestDto;
import ru.evig.gatewayservice.dto.Statement;

import java.util.List;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class GatewayControllerImpl implements GatewayController {
    private final GatewayToStatementClient gatewayToStatementClient;
    private final GatewayToDealClient gatewayToDealClient;

    @Override
    @PostMapping("/offers")
    public List<LoanOfferDto> getLoanOfferList(@RequestBody LoanStatementRequestDto lsrDto) {

        return gatewayToStatementClient.getLoanOfferDtoList(lsrDto);
    }

    @Override
    @PostMapping("/offers/select")
    public void selectLoanOffer(@RequestBody LoanOfferDto loDto) {
        gatewayToStatementClient.selectLoanOffer(loDto);
    }

    @Override
    @PostMapping("/create-credit/{statementId}")
    public void createCredit(@RequestBody FinishRegistrationRequestDto frrDto,
                             @PathVariable String statementId) {
        gatewayToDealClient.createCredit(frrDto, statementId);
    }

    @Override
    @PostMapping("/document/{statementId}/send")
    public void requestForSendingDocuments(@PathVariable String statementId) {
        gatewayToDealClient.requestForSendingDocuments(statementId);
    }

    @Override
    @PostMapping("/document/{statementId}/sign")
    public void requestForSigningDocuments(@PathVariable String statementId) {
        gatewayToDealClient.requestForSigningDocuments(statementId);
    }

    @Override
    @PostMapping("/document/{statementId}/code")
    public void signingDocuments(@PathVariable String statementId) {
        gatewayToDealClient.signingDocuments(statementId);
    }

    @Override
    @GetMapping("/cancel/{statementId}")
    public void clientDenied(@PathVariable String statementId) {
        gatewayToDealClient.clientDenied(statementId);
    }

    @Override
    @GetMapping("/admin/statement/{statementId}")
    public Statement getAdminStatement(@PathVariable String statementId) {

        return gatewayToDealClient.getAdminStatement(statementId);
    }

    @Override
    @GetMapping("/admin/statement")
    public List<Statement> getAdminAllStatements() {

        return gatewayToDealClient.getAdminAllStatements();
    }
}
