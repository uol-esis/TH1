package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.*;
import de.uol.pgdoener.th1.data.entity.Structure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StructureMapper {

    public static StructureDto toDto(Structure entity) {
        return switch (entity.getConverterType()) {
            case REMOVE_GROUPED_HEADER -> new RemoveGroupedHeaderStructureDto(
                    ConverterTypeMapper.toDto(entity.getConverterType()),
                    List.of(entity.getColumns()),
                    List.of(entity.getRows())
            )
                    .startRow(entity.getStartRow())
                    .startColumn(entity.getStartColumn());
            case FILL_EMPTY_ROW -> new FillEmptyRowStructureDto(
                    ConverterTypeMapper.toDto(entity.getConverterType()),
                    List.of(entity.getRows())
            );
            case REMOVE_COLUMN_BY_INDEX -> new RemoveColumnByIndexStructureDto(
                    ConverterTypeMapper.toDto(entity.getConverterType()),
                    List.of(entity.getColumns())
            );
            case REMOVE_ROW_BY_INDEX -> new RemoveRowByIndexStructureDto(
                    ConverterTypeMapper.toDto(entity.getConverterType()),
                    List.of(entity.getRows())
            );
            case ADD_HEADER_NAME -> new AddHeaderNameStructureDto(
                    ConverterTypeMapper.toDto(entity.getConverterType()),
                    List.of(entity.getHeaderNames())
            );
        };
    }

    public static Structure toEntity(StructureDto dto, int position, Long tableStructureId) {
        return switch (dto) {
            case RemoveGroupedHeaderStructureDto structure:
                yield new Structure(
                        null, // ID wird von der Datenbank generiert
                        ConverterTypeMapper.toEntity(dto.getConverterType()),
                        structure.getColumnIndex().toArray(new Integer[0]),
                        structure.getRowIndex().toArray(new Integer[0]),
                        new String[0],
                        structure.getStartRow().orElse(null),
                        null,
                        structure.getStartColumn().orElse(null),
                        null,
                        position,
                        tableStructureId
                );
            case FillEmptyRowStructureDto structure:
                yield new Structure(
                        null, // ID wird von der Datenbank generiert
                        ConverterTypeMapper.toEntity(dto.getConverterType()),
                        new Integer[0],
                        structure.getRowIndex().toArray(new Integer[0]),
                        new String[0],
                        null,
                        null,
                        null,
                        null,
                        position,
                        tableStructureId
                );
            case RemoveColumnByIndexStructureDto structure:
                yield new Structure(
                        null, // ID wird von der Datenbank generiert
                        ConverterTypeMapper.toEntity(dto.getConverterType()),
                        structure.getColumnIndex().toArray(new Integer[0]),
                        new Integer[0],
                        new String[0],
                        null,
                        null,
                        null,
                        null,
                        position,
                        tableStructureId
                );
            case RemoveRowByIndexStructureDto structure:
                yield new Structure(
                        null, // ID wird von der Datenbank generiert
                        ConverterTypeMapper.toEntity(dto.getConverterType()),
                        new Integer[0],
                        structure.getRowIndex().toArray(new Integer[0]),
                        new String[0],
                        null,
                        null,
                        null,
                        null,
                        position,
                        tableStructureId
                );
            case AddHeaderNameStructureDto structure:
                yield new Structure(
                        null, // ID wird von der Datenbank generiert
                        ConverterTypeMapper.toEntity(dto.getConverterType()),
                        new Integer[0],
                        new Integer[0],
                        structure.getHeaderNames().toArray(new String[0]),
                        null,
                        null,
                        null,
                        null,
                        position,
                        tableStructureId
                );
            default:
                throw new IllegalStateException("Unexpected value: " + dto);
        };
    }

    public static StructureSummaryDto toSummaryDto(Structure entity) {
        return new StructureSummaryDto(
                ConverterTypeMapper.toDto(entity.getConverterType())
        );
    }

    public static IStructure toConverterStructure(StructureDto structureDto) {
        return switch (structureDto) {
            case RemoveGroupedHeaderStructureDto structure:
                if (structure.getColumnIndex().isEmpty()) {
                    throw new IllegalArgumentException("Columns missing");
                }
                if (structure.getRowIndex().isEmpty()) {
                    throw new IllegalArgumentException("Rows missing");
                }
                yield new RemoveGroupedHeaderStructure(
                        structure.getColumnIndex().toArray(new Integer[0]),
                        structure.getRowIndex().toArray(new Integer[0]),
                        structure.getStartRow().orElse(null),
                        structure.getStartColumn().orElse(null)
                );
            case FillEmptyRowStructureDto structure:
                if (structure.getRowIndex().isEmpty()) {
                    throw new IllegalArgumentException("Rows missing");
                }
                yield new FillEmptyRowStructure(structure.getRowIndex().toArray(new Integer[0]));
            case RemoveColumnByIndexStructureDto structure:
                if (structure.getColumnIndex().isEmpty()) {
                    throw new IllegalArgumentException("Columns missing");
                }
                yield new RemoveColumnByIndexStructure(structure.getColumnIndex().toArray(new Integer[0]));
            case RemoveRowByIndexStructureDto structure:
                if (structure.getRowIndex().isEmpty()) {
                    throw new IllegalArgumentException("Rows missing");
                }
                yield new RemoveRowByIndexStructure(structure.getRowIndex().toArray(new Integer[0]));
            case AddHeaderNameStructureDto structure:
                if (structure.getHeaderNames().isEmpty()) {
                    throw new IllegalArgumentException("HeaderNames missing");
                }
                yield new HeaderRowStructure(structure.getHeaderNames().toArray(new String[0]));
            case RemoveHeaderStructureDto structure:
                yield new RemoveHeaderStructure(
                        structure.getThreshold().orElse(null),
                        structure.getBlackList().toArray(new String[0]));
            case RemoveFooterStructureDto structure:
                yield new RemoveFooterStructure(
                        structure.getThreshold().orElse(null),
                        structure.getBlackList().toArray(new String[0]));
            default:
                throw new IllegalStateException("Unexpected value: " + structureDto);
        };
    }

}
