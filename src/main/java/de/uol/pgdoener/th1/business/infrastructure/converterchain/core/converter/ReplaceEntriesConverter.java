package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.ReplaceEntriesStructure;
import lombok.RequiredArgsConstructor;

import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class ReplaceEntriesConverter extends Converter {

    private final ReplaceEntriesStructure structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        final int rows = matrix.length;
        final int columns = matrix[0].length;

        final int startRow = structure.startRow().orElse(0);
        final int endRow = structure.endRow().orElse(rows);
        final int startColumn = structure.startColumn().orElse(0);
        final int endColumn = structure.endColumn().orElse(columns);

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

        if (structure.search().isPresent()) {
            final String search = structure.search().get();
            final String replacement = structure.replacement();
            mapper = value -> value.equals(search) ? replacement : value;
        } else if (structure.regexSearch().isPresent()) {
            final String regex = structure.regexSearch().get();
            final String replacement = structure.replacement();
            mapper = value -> value.matches(regex) ? replacement : value;
        } else {
            throwConverterException("Either search or regexSearch must be provided.");
        }
        return mapper;
    }
}
