package de.uol.pgdoener.th1.application.converterchain.builder;

import de.uol.pgdoener.th1.application.converterchain.converter.*;
import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Component
public class ConverterFactory {

    public Converter createConverter(StructureDto structure) {
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
            case RemoveKeywordsStructureDto s -> new RemoveKeywordsConverter(s);
            default -> throw new IllegalArgumentException("Unknown StructureDto type: " + structure.getClass());
        };
    }
}

