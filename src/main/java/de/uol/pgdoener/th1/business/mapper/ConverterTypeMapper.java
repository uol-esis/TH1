package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.business.enums.ConverterType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterTypeMapper {

    public static ConverterType toEntity(ConverterTypeDto converterTypeDto) {
        return ConverterType.valueOf(converterTypeDto.name());
    }

    public static ConverterTypeDto toDto(ConverterType converterType) {
        return ConverterTypeDto.valueOf(converterType.name());
    }

}
