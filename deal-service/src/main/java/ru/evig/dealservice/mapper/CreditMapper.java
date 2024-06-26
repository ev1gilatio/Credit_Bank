package ru.evig.dealservice.mapper;

import org.mapstruct.Mapper;
import ru.evig.dealservice.dto.CreditDto;
import ru.evig.dealservice.entity.Credit;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit dtoToEntity(CreditDto cDto);
}
