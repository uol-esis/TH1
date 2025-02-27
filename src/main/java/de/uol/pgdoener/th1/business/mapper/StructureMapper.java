package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.StructureSummaryDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.*;
import de.uol.pgdoener.th1.data.entity.Structure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StructureMapper {

    public static StructureDto toDto(Structure entity) {
        StructureDto dto = new StructureDto();
        dto.setConverterType(ConverterTypeMapper.toDto(entity.getConverterType()));
        dto.setColumnIndex(List.of(entity.getColumns()));
        dto.setRowIndex(List.of(entity.getRows()));
        dto.setHeaderNames(List.of(entity.getHeaderNames()));
        dto.setStartRow(Optional.ofNullable(entity.getStartRow()));
        dto.setEndRow(Optional.ofNullable(entity.getEndRow()));
        dto.setStartColumn(Optional.ofNullable(entity.getStartColumn()));
        dto.setEndColumn(Optional.ofNullable(entity.getEndColumn()));
        return dto;
    }

    public static Structure toEntity(StructureDto dto, int position, Long tableStructureId) {
        return new Structure(
                null, // ID wird von der Datenbank generiert
                ConverterTypeMapper.toEntity(dto.getConverterType()),
                dto.getColumnIndex().toArray(new Integer[0]),
                dto.getRowIndex().toArray(new Integer[0]),
                dto.getHeaderNames().toArray(new String[0]),
                dto.getStartRow().orElse(null),
                dto.getEndRow().orElse(null),
                dto.getStartColumn().orElse(null),
                dto.getEndColumn().orElse(null),
                position,
                tableStructureId
        );
    }

    public static StructureSummaryDto toSummaryDto(Structure entity) {
        return new StructureSummaryDto(
                ConverterTypeMapper.toDto(entity.getConverterType())
        );
    }

    public static IStructure toConverterStructure(StructureDto structureDto) {
        ConverterTypeDto converterType = structureDto.getConverterType();
        return switch (converterType) {
            case REMOVE_GROUPED_HEADER:
                if (structureDto.getColumnIndex().isEmpty()) {
                    throw new IllegalArgumentException("Columns missing");
                }
                if (structureDto.getRowIndex().isEmpty()) {
                    throw new IllegalArgumentException("Rows missing");
                }
                yield new RemoveGroupedHeaderStructure(
                        structureDto.getColumnIndex().toArray(new Integer[0]),
                        structureDto.getRowIndex().toArray(new Integer[0]),
                        structureDto.getStartRow().orElse(null),
                        structureDto.getStartColumn().orElse(null)
                );
            case FILL_EMPTY_ROW:
                if (structureDto.getRowIndex().isEmpty()) {
                    throw new IllegalArgumentException("Rows missing");
                }
                yield new FillEmptyRowStructure(structureDto.getRowIndex().toArray(new Integer[0]));
            case REMOVE_COLUMN_BY_INDEX:
                if (structureDto.getColumnIndex().isEmpty()) {
                    throw new IllegalArgumentException("Columns missing");
                }
                yield new RemoveColumnByIndexStructure(structureDto.getColumnIndex().toArray(new Integer[0]));
            case REMOVE_ROW_BY_INDEX:
                if (structureDto.getRowIndex().isEmpty()) {
                    throw new IllegalArgumentException("Rows missing");
                }
                yield new RemoveRowByIndexStructure(structureDto.getRowIndex().toArray(new Integer[0]));
            case ADD_HEADER_NAME:
                if (structureDto.getHeaderNames().isEmpty()) {
                    throw new IllegalArgumentException("HeaderNames missing");
                }
                yield new HeaderRowStructure(structureDto.getHeaderNames().toArray(new String[0]));
            case UNKNOWN_DEFAULT_OPEN_API:
                throw new IllegalArgumentException("Unknown converterType");
        };
    }

}
