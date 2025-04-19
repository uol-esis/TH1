package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterFactory {

    public static Converter createConverter(StructureDto structure) {
        return switch (structure) {
            case RemoveGroupedHeaderStructureDto groupedHeaderStructure:
                yield new RemoveGroupedHeaderConverter(groupedHeaderStructure);
            case FillEmptyRowStructureDto fillEmptyRowStructure:
                yield new FillEmptyRowConverter(fillEmptyRowStructure);
            case RemoveColumnByIndexStructureDto removeColumnStructure:
                yield new RemoveColumnByIndexConverter(removeColumnStructure);
            case RemoveRowByIndexStructureDto removeRowStructure:
                yield new RemoveRowByIndexConverter(removeRowStructure);
            case AddHeaderNameStructureDto headerRowStructure:
                yield new AddHeaderRowConverter(headerRowStructure);
            case RemoveHeaderStructureDto removeHeaderStructure:
                yield new RemoveHeaderConverter(removeHeaderStructure);
            case RemoveFooterStructureDto removeFooterStructure:
                yield new RemoveFooterConverter(removeFooterStructure);
            default:
                throw new IllegalStateException("Unexpected value: " + structure);
        };
    }
}
