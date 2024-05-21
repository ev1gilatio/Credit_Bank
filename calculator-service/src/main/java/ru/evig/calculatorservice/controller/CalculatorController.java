package ru.evig.calculatorservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evig.calculatorservice.dto.CreditDto;
import ru.evig.calculatorservice.dto.LoanOfferDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;
import ru.evig.calculatorservice.dto.ScoringDataDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    @PostMapping("/offers")
    private List<LoanOfferDto> someVoid1(LoanStatementRequestDto loanStatementRequestDto) {

        return new ArrayList<>();
    }

    @PostMapping("/calc")
    private CreditDto someVoid2(ScoringDataDto scoringDataDto) {

        return new CreditDto();
    }
}
