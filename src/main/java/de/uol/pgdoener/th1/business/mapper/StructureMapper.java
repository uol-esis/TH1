package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.data.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StructureMapper {

    public static StructureDto toDto(Structure entity) {
        return switch (entity) {
            case RemoveGroupedHeaderStructure structure -> new RemoveGroupedHeaderStructureDto(
                    ConverterTypeDto.REMOVE_GROUPED_HEADER,
                    List.of(structure.getRows()),
                    List.of(structure.getColumns())
            )
                    .startRow(structure.getStartRow())
                    .startColumn(structure.getStartColumn());
            case FillEmptyRowStructure structure -> new FillEmptyRowStructureDto(
                    ConverterTypeDto.FILL_EMPTY_ROW,
                    List.of(structure.getRows())
            );
            case RemoveColumnByIndexStructure structure -> new RemoveColumnByIndexStructureDto(
                    ConverterTypeDto.REMOVE_COLUMN_BY_INDEX,
                    List.of(structure.getColumns())
            );
            case RemoveRowByIndexStructure structure -> new RemoveRowByIndexStructureDto(
                    ConverterTypeDto.REMOVE_ROW_BY_INDEX,
                    List.of(structure.getRows())
            );
            case HeaderRowStructure structure -> new AddHeaderNameStructureDto(
                    ConverterTypeDto.ADD_HEADER_NAME,
                    List.of(structure.getHeaderNames())
            );
            case RemoveHeaderStructure structure -> new RemoveHeaderStructureDto(
                    ConverterTypeDto.REMOVE_HEADER
            )
                    .threshold(structure.getThreshold())
                    .blackList(List.of(structure.getBlackList()));
            case RemoveFooterStructure structure -> new RemoveFooterStructureDto(
                    ConverterTypeDto.REMOVE_FOOTER
            )
                    .threshold(structure.getThreshold())
                    .blackList(List.of(structure.getBlackList()));
            case RemoveTrailingColumnStructure structure -> new RemoveTrailingColumnStructureDto(
                    ConverterTypeDto.REMOVE_TRAILING_COLUMN
            )
                    .threshold(structure.getThreshold())
                    .blackList(List.of(structure.getBlackList()));
            case ReplaceEntriesStructure structure -> new ReplaceEntriesStructureDto(
                    ConverterTypeDto.REPLACE_ENTRIES,
                    structure.getReplacement()
            )
                    .search(structure.getSearch())
                    .regexSearch(structure.getRegexSearch())
                    .startRow(structure.getStartRow())
                    .endRow(structure.getEndRow())
                    .startColumn(structure.getStartColumn())
                    .endColumn(structure.getEndColumn());
            case SplitRowStructure structure -> new SplitRowStructureDto(
                    ConverterTypeDto.SPLIT_ROW,
                    structure.getColumnIndex()
            )
                    .delimiter(structure.getDelimiter())
                    .startRow(structure.getStartRow())
                    .endRow(structure.getEndRow());
            default -> throw new IllegalStateException("Unexpected value: " + entity);
        };
    }

    public static Structure toEntity(StructureDto dto, int position, Long tableStructureId) {
        return switch (dto) {
            case RemoveGroupedHeaderStructureDto structure -> new RemoveGroupedHeaderStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getColumnIndex().toArray(new Integer[0]),
                    structure.getRowIndex().toArray(new Integer[0]),
                    structure.getStartRow().orElse(null),
                    structure.getStartColumn().orElse(null)
            );
            case FillEmptyRowStructureDto structure -> new FillEmptyRowStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getRowIndex().toArray(new Integer[0])
            );
            case RemoveColumnByIndexStructureDto structure -> new RemoveColumnByIndexStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getColumnIndex().toArray(new Integer[0])
            );
            case RemoveRowByIndexStructureDto structure -> new RemoveRowByIndexStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getRowIndex().toArray(new Integer[0])
            );
            case AddHeaderNameStructureDto structure -> new HeaderRowStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getHeaderNames().toArray(new String[0])
            );
            case RemoveHeaderStructureDto structure -> new RemoveHeaderStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getThreshold().orElse(null),
                    structure.getBlackList().toArray(new String[0])
            );
            case RemoveFooterStructureDto structure -> new RemoveFooterStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getThreshold().orElse(null),
                    structure.getBlackList().toArray(new String[0])
            );
            case RemoveTrailingColumnStructureDto structure -> new RemoveTrailingColumnStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getThreshold().orElse(null),
                    structure.getBlackList().toArray(new String[0])
            );
            case ReplaceEntriesStructureDto structure -> new ReplaceEntriesStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getReplacement(),
                    structure.getSearch().orElse(null),
                    structure.getRegexSearch().orElse(null),
                    structure.getStartRow().orElse(null),
                    structure.getEndRow().orElse(null),
                    structure.getStartColumn().orElse(null),
                    structure.getEndColumn().orElse(null)
            );
            case SplitRowStructureDto structure -> new SplitRowStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getColumnIndex(),
                    structure.getDelimiter().orElse(null),
                    structure.getStartRow().orElse(null),
                    structure.getEndRow().orElse(null)
            );
            case RemoveInvalidRowsStructureDto structure -> new removeInvalidRowStructure(
                    null,
                    position,
                    tableStructureId,
                    structure.getThreshold().orElse(null),
                    structure.getBlackList().toArray(new String[0])
            );
        };
    }

    public static StructureSummaryDto toSummaryDto(Structure entity) {
        return new StructureSummaryDto(
                ConverterTypeMapper.toDto(entity)
        );
    }

}
