package ru.evig.statementservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.evig.statementservice.dto.LoanOfferDto;
import ru.evig.statementservice.dto.LoanStatementRequestDto;

import java.util.List;

@FeignClient(value = "statement", url = "${deal-service.url}")
public interface StatementClient {

    @PostMapping(value = "/deal/statement", consumes = "application/json")
    List<LoanOfferDto> getLoanOfferDtoList(LoanStatementRequestDto lsrDto);

    @PostMapping(value = "/deal/offer/select", consumes = "application/json")
    void selectLoanOffer(LoanOfferDto loDto);
}
