package ru.evig.dealservice.mapper;

import org.springframework.stereotype.Component;
import ru.evig.dealservice.dto.FinishRegistrationRequestDto;
import ru.evig.dealservice.dto.ScoringDataDto;
import ru.evig.dealservice.entity.Statement;

@Component
public class ScoringDataMapper {
    public ScoringDataDto frrDtoToSdDto(FinishRegistrationRequestDto frrDto, Statement statement) {

        return ScoringDataDto.builder()
                .amount(statement.getAppliedOffer().getTotalAmount())
                .term(statement.getAppliedOffer().getTerm())
                .firstName(statement.getClientId().getFirstName())
                .lastName(statement.getClientId().getLastName())
                .middleName(statement.getClientId().getMiddleName())
                .gender(frrDto.getGender())
                .birthday(statement.getClientId().getBirthDate())
                .passportSeries(statement.getClientId().getPassport().getSeries())
                .passportNumber(statement.getClientId().getPassport().getNumber())
                .passportIssueDate(frrDto.getPassportIssueDate())
                .passportIssueBranch(frrDto.getPassportIssueBranch())
                .maritalStatus(frrDto.getMaritalStatus())
                .dependentAmount(frrDto.getDependentAmount())
                .employment(frrDto.getEmployment())
                .accountNumber(frrDto.getAccountNumber())
                .isInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnable())
                .isSalaryClient(statement.getAppliedOffer().getIsSalaryClient())
                .build();
    }
}
