package ru.evig.gatewayservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.evig.gatewayservice.dto.LoanOfferDto;
import ru.evig.gatewayservice.dto.LoanStatementRequestDto;

import java.util.List;

@FeignClient(value = "toStatement", url = "${statement-service.url}")
public interface GatewayToStatementClient {

    @PostMapping(value = "/statement", consumes = "application/json")
    List<LoanOfferDto> getLoanOfferDtoList(LoanStatementRequestDto lsrDto);

    @PostMapping(value = "/statement/offer", consumes = "application/json")
    void selectLoanOffer(LoanOfferDto loDto);
}
