package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.ReplaceEntriesStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;

import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class ReplaceEntriesConverter extends Converter {

    private final ReplaceEntriesStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        final int rows = matrix.length;
        final int columns = matrix[0].length;

        final int startRow = structure.getStartRow().orElse(0);
        final int endRow = structure.getEndRow().orElse(rows);
        final int startColumn = structure.getStartColumn().orElse(0);
        final int endColumn = structure.getEndColumn().orElse(columns);

        final UnaryOperator<String> mapper = getMapper();

        for (int i = startRow; i < endRow; i++) {
            for (int j = startColumn; j < endColumn; j++) {
                matrix[i][j] = mapper.apply(matrix[i][j]);
            }
        }

        return super.handleRequest(matrix);
    }

    private UnaryOperator<String> getMapper() {
        UnaryOperator<String> mapper = null;

        if (structure.getSearch().isPresent()) {
            final String search = structure.getSearch().get();
            final String replacement = structure.getReplacement();
            mapper = value -> value.equals(search) ? replacement : value;
        } else if (structure.getRegexSearch().isPresent()) {
            final String regex = structure.getRegexSearch().get();
            final String replacement = structure.getReplacement();
            mapper = value -> value.matches(regex) ? replacement : value;
        } else {
            throwConverterException("Either search or regexSearch must be provided.");
        }
        return mapper;
    }
}
