package ru.evig.dealservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.evig.dealservice.dto.CreditDto;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;
import ru.evig.dealservice.dto.ScoringDataDto;

import java.util.List;

@FeignClient(value = "deal", url = "${calculator-service.url}")
public interface DealClient {

    @PostMapping(value = "/calculator/offers", consumes = "application/json")
    List<LoanOfferDto> getLoanOfferDtoList(LoanStatementRequestDto lsrDto);

    @PostMapping(value = "/calculator/calc", consumes = "application/json")
    CreditDto getCreditDto(ScoringDataDto sdDto);
}
