package de.uol.pgdoener.th1.business.infrastructure.csv_converter.core;

import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.enums.ConverterType;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.converter.FillEmptyCellsConverter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.converter.RemoveColumnByIndexConverter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.converter.RemoveGroupedHeaderConverter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.converter.RemoveRowByIndexConverter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.FillEmptyStructure;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.RemoveColumnByIndexStructure;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.RemoveGroupedHeaderStructure;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.RemoveRowByIndexStructure;


abstract public class ConverterFactory {

    static public Converter createValidator(StructureDto structureDto) {
        ConverterType converterType = structureDto.converterType();
        return switch (converterType) {
            case REMOVE_GROUPED_HEADER:
                var groupedHeaderStructure = new RemoveGroupedHeaderStructure(
                        structureDto.columns().orElseThrow(() -> new IllegalArgumentException("Columns missing")),
                        structureDto.rows().orElseThrow(() -> new IllegalArgumentException("Rows missing")),
                        structureDto.startRow().orElseThrow(() -> new IllegalArgumentException("Start row missing")),
                        structureDto.endRow().orElseThrow(() -> new IllegalArgumentException("End row missing")),
                        structureDto.startColumn().orElseThrow(() -> new IllegalArgumentException("Start column missing")),
                        structureDto.endColumn().orElseThrow(() -> new IllegalArgumentException("End column missing"))
                );
                yield new RemoveGroupedHeaderConverter(groupedHeaderStructure);
            case FILL_EMPTY_CELLS:
                var fillEmptyStructure = new FillEmptyStructure(
                        structureDto.rows().orElseThrow(() -> new IllegalArgumentException("Rows missing"))
                );
                yield new FillEmptyCellsConverter(fillEmptyStructure);
            case REMOVE_COLUMN_BY_INDEX:
                var removeColumnStructure = new RemoveColumnByIndexStructure(
                        structureDto.columns().orElseThrow(() -> new IllegalArgumentException("Columns missing"))
                );
                yield new RemoveColumnByIndexConverter(removeColumnStructure);
            case REMOVE_ROW_BY_INDEX:
                var removeRowStructure = new RemoveRowByIndexStructure(
                        structureDto.rows().orElseThrow(() -> new IllegalArgumentException("Rows missing"))
                );
                yield new RemoveRowByIndexConverter(removeRowStructure);
        };
    }
}
