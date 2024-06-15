package ru.evig.dealservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;

import java.util.List;

@FeignClient(value = "deal", url = "http://localhost:8080")
public interface DealClient {

    @PostMapping(value = "/calculator/offers", consumes = "application/json")
    List<LoanOfferDto> getLoanOfferDtoList(LoanStatementRequestDto lsrDto);
}
