package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.data.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterTypeMapper {

    public static ConverterTypeDto toDto(Structure structure) {
        return switch (structure) {
            case FillEmptyRowStructure ignored -> ConverterTypeDto.FILL_EMPTY_ROW;
            case HeaderRowStructure ignored -> ConverterTypeDto.ADD_HEADER_NAME;
            case RemoveGroupedHeaderStructure ignored -> ConverterTypeDto.REMOVE_GROUPED_HEADER;
            case RemoveColumnByIndexStructure ignored -> ConverterTypeDto.REMOVE_COLUMN_BY_INDEX;
            case RemoveRowByIndexStructure ignored -> ConverterTypeDto.REMOVE_ROW_BY_INDEX;
            case RemoveHeaderStructure ignored -> ConverterTypeDto.REMOVE_HEADER;
            case RemoveFooterStructure ignored -> ConverterTypeDto.REMOVE_FOOTER;
            default -> throw new IllegalStateException("Unexpected value: " + structure);
        };
    }

}
