package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ConverterFactory {

    public static Converter createConverter(StructureDto structure) {
        return switch (structure) {
            case RemoveGroupedHeaderStructureDto s -> new RemoveGroupedHeaderConverter(s);
            case FillEmptyRowStructureDto s -> new FillEmptyRowConverter(s);
            case FillEmptyColumnStructureDto s -> new FillEmptyColumnConverter(s);
            case RemoveColumnByIndexStructureDto s -> new RemoveColumnByIndexConverter(s);
            case RemoveRowByIndexStructureDto s -> new RemoveRowByIndexConverter(s);
            case AddHeaderNameStructureDto s -> new AddHeaderRowConverter(s);
            case RemoveHeaderStructureDto s -> new RemoveHeaderConverter(s);
            case RemoveFooterStructureDto s -> new RemoveFooterConverter(s);
            case RemoveTrailingColumnStructureDto s -> new RemoveTrailingColumnConverter(s);
            case RemoveLeadingColumnStructureDto s -> new RemoveLeadingColumnConverter(s);
            case ReplaceEntriesStructureDto s -> new ReplaceEntriesConverter(s);
            case SplitRowStructureDto s -> new SplitRowConverter(s);
            case RemoveInvalidRowsStructureDto s -> new RemoveInvalidRowsConverter(s);
            case MergeColumnsStructureDto s -> new MergeColumnsConverter(s);
            case TransposeMatrixStructureDto s -> new TransposeMatrixConverter(s);
            case PivotMatrixStructureDto s -> new PivotMatrixConverter(s);
        };
    }
}
