package ru.evig.dossierservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "dossier", url = "${deal-service.url}")
public interface DossierClient {

    @GetMapping(value = "/deal/document/{statementId}/documentation", consumes = "application/json")
    String getDocumentation(@PathVariable String statementId);

    @GetMapping(value = "/deal/document/{statementId}/ses-code", consumes = "application/json")
    String getSesCode(@PathVariable String statementId);
}
