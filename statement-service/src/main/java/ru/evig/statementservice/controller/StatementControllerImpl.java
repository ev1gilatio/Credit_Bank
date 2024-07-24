package ru.evig.statementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evig.statementservice.StatementClient;
import ru.evig.statementservice.dto.LoanOfferDto;
import ru.evig.statementservice.dto.LoanStatementRequestDto;
import ru.evig.statementservice.service.StatementService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementControllerImpl implements StatementController {
    private final StatementClient statementClient;
    private final StatementService service;

    @Override
    @PostMapping()
    public List<LoanOfferDto> getLoanOfferList(@Valid @RequestBody LoanStatementRequestDto lsrDto) {
        service.makePreScoring(lsrDto);

        return statementClient.getLoanOfferDtoList(lsrDto);
    }

    @Override
    @PostMapping("/offer")
    public void selectLoanOffer(@Valid @RequestBody LoanOfferDto loDto) {
        statementClient.selectLoanOffer(loDto);
    }
}
