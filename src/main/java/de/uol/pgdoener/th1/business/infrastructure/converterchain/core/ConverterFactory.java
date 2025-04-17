package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterFactory {

    public static Converter createConverter(StructureDto structure) {
        return switch (structure) {
            case RemoveGroupedHeaderStructureDto groupedHeaderStructure ->
                    new RemoveGroupedHeaderConverter(groupedHeaderStructure);
            case FillEmptyRowStructureDto fillEmptyRowStructure -> new FillEmptyRowConverter(fillEmptyRowStructure);
            case RemoveColumnByIndexStructureDto removeColumnStructure ->
                    new RemoveColumnByIndexConverter(removeColumnStructure);
            case RemoveRowByIndexStructureDto removeRowStructure -> new RemoveRowByIndexConverter(removeRowStructure);
            case AddHeaderNameStructureDto headerRowStructure -> new AddHeaderRowConverter(headerRowStructure);
            default -> throw new IllegalStateException("Unexpected value: " + structure);
        };
    }
}
