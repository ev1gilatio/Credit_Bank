package ru.evig.dealservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {

    private final DealClient client;

    @PostMapping("/statement")
    private List<LoanOfferDto> makeLoanOfferList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {

        return client.getLoanOfferDtoList(loanStatementRequestDto);
    }
}
