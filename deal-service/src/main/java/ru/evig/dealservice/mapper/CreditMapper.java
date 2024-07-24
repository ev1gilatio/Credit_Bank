package ru.evig.dealservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.evig.dealservice.dto.CreditDto;
import ru.evig.dealservice.entity.Credit;
import ru.evig.dealservice.enums.CreditStatus;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit dtoToEntity(CreditDto cDto);
    Credit statusToEntity(@MappingTarget Credit oldCredit, CreditStatus creditStatus);
}
