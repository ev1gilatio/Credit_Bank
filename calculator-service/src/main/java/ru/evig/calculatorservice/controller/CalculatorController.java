package ru.evig.calculatorservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evig.calculatorservice.dto.CreditDto;
import ru.evig.calculatorservice.dto.LoanOfferDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;
import ru.evig.calculatorservice.dto.ScoringDataDto;
import ru.evig.calculatorservice.service.CalculatorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {
    private final CalculatorService service;

    @PostMapping("/offers")
    private List<LoanOfferDto> getLoanOffer(@Valid @RequestBody
                                                LoanStatementRequestDto loanStatementRequestDto) {

        return service.getLoanOfferList(loanStatementRequestDto);
    }

    @PostMapping("/calc")
    private CreditDto someVoid2(ScoringDataDto scoringDataDto) {

        return new CreditDto();
    }
}
