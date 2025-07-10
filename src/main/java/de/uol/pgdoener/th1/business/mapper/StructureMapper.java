package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.data.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static de.uol.pgdoener.th1.data.entity.MatchType.CONTAINS;
import static de.uol.pgdoener.th1.data.entity.MatchType.EQUALS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class StructureMapper {

    public static StructureDto toDto(Structure entity) {
        return switch (entity) {
            case RemoveGroupedHeaderStructure structure -> new RemoveGroupedHeaderStructureDto(
                    ConverterTypeDto.REMOVE_GROUPED_HEADER,
                    List.of(structure.getRows()),
                    List.of(structure.getColumns())
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .startRow(structure.getStartRow())
                    .startColumn(structure.getStartColumn());
            case FillEmptyRowStructure structure -> new FillEmptyRowStructureDto(
                    ConverterTypeDto.FILL_EMPTY_ROW,
                    List.of(structure.getRows())
            )
                    .name(structure.getName())
                    .description(structure.getDescription());
            case FillEmptyColumnStructure structure -> new FillEmptyColumnStructureDto(
                    ConverterTypeDto.FILL_EMPTY_COLUMN,
                    List.of(structure.getColumns())
            )
                    .name(structure.getName())
                    .description(structure.getDescription());
            case RemoveColumnByIndexStructure structure -> new RemoveColumnByIndexStructureDto(
                    ConverterTypeDto.REMOVE_COLUMN_BY_INDEX,
                    List.of(structure.getColumns())
            )
                    .name(structure.getName())
                    .description(structure.getDescription());
            case RemoveRowByIndexStructure structure -> new RemoveRowByIndexStructureDto(
                    ConverterTypeDto.REMOVE_ROW_BY_INDEX,
                    List.of(structure.getRows())
            )
                    .name(structure.getName())
                    .description(structure.getDescription());
            case HeaderRowStructure structure -> new AddHeaderNameStructureDto(
                    ConverterTypeDto.ADD_HEADER_NAME,
                    List.of(structure.getHeaderNames())
            )
                    .name(structure.getName())
                    .description(structure.getDescription());
            case RemoveHeaderStructure structure -> new RemoveHeaderStructureDto(
                    ConverterTypeDto.REMOVE_HEADER
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .threshold(structure.getThreshold())
                    .blockList(List.of(structure.getBlackList()));
            case RemoveFooterStructure structure -> new RemoveFooterStructureDto(
                    ConverterTypeDto.REMOVE_FOOTER
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .threshold(structure.getThreshold())
                    .blockList(List.of(structure.getBlackList()));
            case RemoveInvalidRowStructure structure -> new RemoveInvalidRowsStructureDto(
                    ConverterTypeDto.REMOVE_INVALID_ROWS
            )
                    .threshold(structure.getThreshold())
                    .blockList(List.of(structure.getBlackList()));
            case RemoveTrailingColumnStructure structure -> new RemoveTrailingColumnStructureDto(
                    ConverterTypeDto.REMOVE_TRAILING_COLUMN
            )
                    .blockList(List.of(structure.getBlackList()));
            case RemoveLeadingColumnStructure structure -> new RemoveTrailingColumnStructureDto(
                    ConverterTypeDto.REMOVE_LEADING_COLUMN
            )
                    .blockList(List.of(structure.getBlackList()));
            case ReplaceEntriesStructure structure -> new ReplaceEntriesStructureDto(
                    ConverterTypeDto.REPLACE_ENTRIES,
                    structure.getReplacement()
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .search(structure.getSearch())
                    .regexSearch(structure.getRegexSearch())
                    .startRow(structure.getStartRow())
                    .endRow(structure.getEndRow())
                    .columnIndex(structure.getColumns());
            case SplitRowStructure structure -> new SplitRowStructureDto(
                    ConverterTypeDto.SPLIT_ROW,
                    structure.getColumnIndex()
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .delimiter(structure.getDelimiter())
                    .startRow(structure.getStartRow())
                    .endRow(structure.getEndRow());
            case MergeColumnsStructure structure -> new MergeColumnsStructureDto(
                    ConverterTypeDto.MERGE_COLUMNS,
                    List.of(structure.getColumns()),
                    structure.getHeaderName()
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .precedenceOrder(List.of(structure.getPrecedenceOrder()));
            case TransposeMatrixStructure structure -> new TransposeMatrixStructureDto(
                    ConverterTypeDto.TRANSPOSE_MATRIX
            )
                    .name(structure.getName())
                    .description(structure.getDescription());
            case PivotMatrixStructure structure -> new PivotMatrixStructureDto(
                    ConverterTypeDto.PIVOT_MATRIX
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .pivotField(structure.getPivotField())
                    .keysToCarryForward(Arrays.asList(structure.getKeysToCarryForward()))
                    .blockIndices(List.of(structure.getBlockIndices()));
            case RemoveKeywordsStructure structure -> new RemoveKeywordsStructureDto(
                    ConverterTypeDto.REMOVE_KEYWORD,
                    structure.getRemoveRows(),
                    structure.getRemoveColumns(),
                    structure.getIgnoreCase(),
                    convertMatchTypeToDto(structure.getMatchType())
            )
                    .name(structure.getName())
                    .description(structure.getDescription())
                    .keywords(Arrays.asList(structure.getKeywords()));
            default -> throw new IllegalStateException("Unexpected value: " + entity);
        };
    }

    public static Structure toEntity(StructureDto dto, int position, Long tableStructureId) {
        return switch (dto) {
            case RemoveGroupedHeaderStructureDto structure -> new RemoveGroupedHeaderStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getColumnIndex().toArray(new Integer[0]),
                    structure.getRowIndex().toArray(new Integer[0]),
                    structure.getStartRow().orElse(null),
                    structure.getStartColumn().orElse(null)
            );
            case FillEmptyRowStructureDto structure -> new FillEmptyRowStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getRowIndex().toArray(new Integer[0])
            );
            case FillEmptyColumnStructureDto structure -> new FillEmptyColumnStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getColumnIndex().toArray(new Integer[0])
            );
            case RemoveColumnByIndexStructureDto structure -> new RemoveColumnByIndexStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getColumnIndex().toArray(new Integer[0])
            );
            case RemoveRowByIndexStructureDto structure -> new RemoveRowByIndexStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getRowIndex().toArray(new Integer[0])
            );
            case AddHeaderNameStructureDto structure -> new HeaderRowStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getHeaderNames().toArray(new String[0])
            );
            case RemoveHeaderStructureDto structure -> new RemoveHeaderStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getThreshold().orElse(null),
                    structure.getBlockList().toArray(new String[0])
            );
            case RemoveFooterStructureDto structure -> new RemoveFooterStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getThreshold().orElse(null),
                    structure.getBlockList().toArray(new String[0])
            );
            case RemoveTrailingColumnStructureDto structure -> new RemoveTrailingColumnStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getBlockList().toArray(new String[0])
            );
            case RemoveLeadingColumnStructureDto structure -> new RemoveLeadingColumnStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getBlockList().toArray(new String[0])
            );
            case ReplaceEntriesStructureDto structure -> new ReplaceEntriesStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getReplacement(),
                    structure.getSearch().orElse(null),
                    structure.getRegexSearch().orElse(null),
                    structure.getColumnIndex(),
                    structure.getStartRow().orElse(null),
                    structure.getEndRow().orElse(null)
            );
            case SplitRowStructureDto structure -> new SplitRowStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getColumnIndex(),
                    structure.getDelimiter().orElse(null),
                    structure.getStartRow().orElse(null),
                    structure.getEndRow().orElse(null)
            );
            case RemoveInvalidRowsStructureDto structure -> new RemoveInvalidRowStructure(
                    null,
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getThreshold().orElse(null),
                    structure.getBlockList().toArray(new String[0])
            );
            case MergeColumnsStructureDto structure -> new MergeColumnsStructure(
                    null, // ID wird von der Datenbank generiert
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getColumnIndex().toArray(new Integer[0]),
                    structure.getHeaderName(),
                    structure.getPrecedenceOrder().toArray(new Integer[0])
            );
            case TransposeMatrixStructureDto structure -> new TransposeMatrixStructure(
                    null,
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null)
            );
            case PivotMatrixStructureDto structure -> new PivotMatrixStructure(
                    null,
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getPivotField(),
                    structure.getBlockIndices().toArray(new Integer[0]),
                    structure.getKeysToCarryForward().toArray(new String[0])
            );
            case RemoveKeywordsStructureDto structure -> new RemoveKeywordsStructure(
                    null,
                    position,
                    tableStructureId,
                    structure.getName().orElse(null),
                    structure.getDescription().orElse(null),
                    structure.getKeywords().toArray(new String[0]),
                    structure.isRemoveRows(),
                    structure.isRemoveColumns(),
                    structure.isIgnoreCase(),
                    convertMatchTypeToEntity(structure.getMatchType())
            );
        };
    }

    private static MatchType convertMatchTypeToEntity(RemoveKeywordsStructureDto.MatchTypeEnum dtoEnum) {
        if (dtoEnum == null) {
            return EQUALS; // Default oder null-behandlung
        }
        return switch (dtoEnum) {
            case CONTAINS -> CONTAINS;
            case EQUALS -> EQUALS;
            default -> throw new IllegalArgumentException("Unknown matchType: " + dtoEnum);
        };
    }


    private static RemoveKeywordsStructureDto.MatchTypeEnum convertMatchTypeToDto(MatchType matchType) {
        if (matchType == null) {
            return RemoveKeywordsStructureDto.MatchTypeEnum.EQUALS;
        }
        return switch (matchType) {
            case CONTAINS -> RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS;
            case EQUALS -> RemoveKeywordsStructureDto.MatchTypeEnum.EQUALS;
        };
    }

    public static StructureSummaryDto toSummaryDto(Structure entity) {
        return new StructureSummaryDto(
                ConverterTypeMapper.toDto(entity)
        );
    }

}
