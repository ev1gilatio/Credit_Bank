package ru.evig.calculatorservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.evig.calculatorservice.Exception.LoanRejectedException;
import ru.evig.calculatorservice.Exception.TooYoungForCreditException;
import ru.evig.calculatorservice.dto.CreditDto;
import ru.evig.calculatorservice.dto.LoanOfferDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;
import ru.evig.calculatorservice.dto.ScoringDataDto;
import ru.evig.calculatorservice.service.CalculatorService;

import javax.validation.Valid;
import java.util.List;

//Swagger - http://localhost:8080/swagger-ui/index.html

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CalculatorService service;

    @PostMapping("/offers")
    private List<LoanOfferDto> getLoanOffer(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {

        return service.getLoanOfferDtoList(loanStatementRequestDto);
    }

    @PostMapping("/calc")
    private CreditDto getCreditCalculation(@Valid @RequestBody ScoringDataDto scoringDataDto) {

        return service.getCreditDto(scoringDataDto);
    }

    @ExceptionHandler(TooYoungForCreditException.class)
    public ResponseEntity<ErrorMessage> handleException(TooYoungForCreditException e, WebRequest r) {

        return badRequestResponse(e, r);
    }

    @ExceptionHandler(LoanRejectedException.class)
    public ResponseEntity<ErrorMessage> handleException(LoanRejectedException e, WebRequest r) {

        return badRequestResponse(e, r);
    }

    private ResponseEntity<ErrorMessage> badRequestResponse(RuntimeException e, WebRequest r) {
        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), r.getDescription(false));

        log.error(logMessage, e);

        return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
