package ru.evig.gatewayservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.evig.gatewayservice.dto.FinishRegistrationRequestDto;
import ru.evig.gatewayservice.dto.Statement;

import java.util.List;


@FeignClient(value = "toDeal", url = "${deal-service.url}")
public interface GatewayToDealClient {

    @PostMapping(value = "/deal/calculate/{statementId}", consumes = "application/json")
    void createCredit(FinishRegistrationRequestDto frrDto,
                      @PathVariable String statementId);

    @PostMapping(value = "/deal/document/{statementId}/send", consumes = "application/json")
    void requestForSendingDocuments(@PathVariable String statementId);

    @PostMapping(value = "/deal/document/{statementId}/sign", consumes = "application/json")
    void requestForSigningDocuments(@PathVariable String statementId);

    @PostMapping(value = "/deal/document/{statementId}/code", consumes = "application/json")
    void signingDocuments(@PathVariable String statementId);

    @GetMapping(value = "/deal/cancel/{statementId}", consumes = "application/json")
    void clientDenied(@PathVariable String statementId);

    @GetMapping(value = "/deal/admin/statement/{statementId}")
    Statement getAdminStatement(@PathVariable String statementId);

    @GetMapping(value = "/deal/admin/statement")
    List<Statement> getAdminAllStatements();
}
