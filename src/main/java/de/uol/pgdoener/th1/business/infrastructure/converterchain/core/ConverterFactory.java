package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.FillEmptyCellsConverter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.RemoveColumnByIndexConverter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.RemoveGroupedHeaderConverter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.RemoveRowByIndexConverter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterFactory {

    public static Converter createConverter(IStructure structure) {
        return switch (structure) {
            case RemoveGroupedHeaderStructure groupedHeaderStructure:
                yield new RemoveGroupedHeaderConverter(groupedHeaderStructure);
            case FillEmptyStructure fillEmptyStructure:
                yield new FillEmptyCellsConverter(fillEmptyStructure);
            case RemoveColumnByIndexStructure removeColumnStructure:
                yield new RemoveColumnByIndexConverter(removeColumnStructure);
            case RemoveRowByIndexStructure removeRowStructure:
                yield new RemoveRowByIndexConverter(removeRowStructure);
        };
    }
}
