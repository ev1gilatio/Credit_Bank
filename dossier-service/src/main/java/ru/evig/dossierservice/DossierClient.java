package ru.evig.dossierservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.evig.dossierservice.dto.Statement;

@FeignClient(value = "dossier", url = "${deal-service.url}")
public interface DossierClient {

    @GetMapping(value = "/deal/document/{statementId}", consumes = "application/json")
    Statement getStatement(@PathVariable String statementId);

    @GetMapping(value = "/deal/document/{statementId}/ses-code", consumes = "application/json")
    String getSesCode(@PathVariable String statementId);
}
