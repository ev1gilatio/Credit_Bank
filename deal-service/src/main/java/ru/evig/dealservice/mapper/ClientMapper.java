package ru.evig.dealservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import ru.evig.dealservice.dto.FinishRegistrationRequestDto;
import ru.evig.dealservice.dto.LoanStatementRequestDto;
import ru.evig.dealservice.dto.PassportDto;
import ru.evig.dealservice.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "birthDate", source = "birthday")
    @Mappings(
            {
                    @Mapping(target = "gender", ignore = true),
                    @Mapping(target = "maritalStatus", ignore = true),
                    @Mapping(target = "dependentAmount", ignore = true),
                    @Mapping(target = "passport", ignore = true),
                    @Mapping(target = "employment", ignore = true),
                    @Mapping(target = "accountNumber", ignore = true)
            }
    )
    Client loanStatementRequestDtoToEntity(LoanStatementRequestDto lsrDto);

    @Mapping(target = "passport", ignore = true)
    Client finishRegistrationDtoToEntity(@MappingTarget Client oldClient, FinishRegistrationRequestDto frrDto);
    Client passportDtoToEntity(@MappingTarget Client oldClient, PassportDto passport);

}
