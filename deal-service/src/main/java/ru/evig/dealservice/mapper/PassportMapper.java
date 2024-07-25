package ru.evig.dealservice.mapper;

import org.springframework.stereotype.Component;
import ru.evig.dealservice.dto.FinishRegistrationRequestDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;
import ru.evig.dealservice.dto.PassportDto;

@Component
public class PassportMapper {
    public PassportDto lsrDtoToPassportDto(LoanStatementRequestDto lsrDto) {

        return PassportDto.builder()
                .series(lsrDto.getPassportSeries())
                .number(lsrDto.getPassportNumber())
                .build();
    }

    public PassportDto frrDtoToPassportDto(FinishRegistrationRequestDto frrDto, PassportDto passport) {
        PassportDto.PassportDtoBuilder passportToBuilder = passport.toBuilder();
        passport = passportToBuilder
                .issueDate(frrDto.getPassportIssueDate())
                .issueBranch(frrDto.getPassportIssueBranch())
                .build();

        return passport;
    }
}
