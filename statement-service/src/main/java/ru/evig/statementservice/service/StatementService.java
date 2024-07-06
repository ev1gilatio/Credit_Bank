package ru.evig.statementservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.evig.statementservice.dto.LoanStatementRequestDto;
import ru.evig.statementservice.exception.TooYoungForCreditException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class StatementService {
    private final Integer MIN_AGE = 18;

    public void makePreScoring(LoanStatementRequestDto lsrDto) {
        log.info("Input data for makePreScoring = " + lsrDto);

        if (LocalDate.from(lsrDto.getBirthday()).until(LocalDate.now(), ChronoUnit.YEARS) < MIN_AGE) {
            throw new TooYoungForCreditException("The person is younger than 18");
        }
    }
}
