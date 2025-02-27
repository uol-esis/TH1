package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.*;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterFactory {

    public static Converter createConverter(IStructure structure) {
        return switch (structure) {
            case RemoveGroupedHeaderStructure groupedHeaderStructure:
                yield new RemoveGroupedHeaderConverter(groupedHeaderStructure);
            case FillEmptyRowStructure fillEmptyRowStructure:
                yield new FillEmptyRowConverter(fillEmptyRowStructure);
            case RemoveColumnByIndexStructure removeColumnStructure:
                yield new RemoveColumnByIndexConverter(removeColumnStructure);
            case RemoveRowByIndexStructure removeRowStructure:
                yield new RemoveRowByIndexConverter(removeRowStructure);
            case HeaderRowStructure headerRowStructure:
                yield new AddHeaderRowConverter(headerRowStructure);
        };
    }
}
