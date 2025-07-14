package de.uol.pgdoener.th1.application.converterchain.converter;

import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.ReplaceEntriesStructureDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class ReplaceEntriesConverter extends Converter {

    private final ReplaceEntriesStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        final int rows = matrix.length;

        List<Integer> columnIndex = structure.getColumnIndex();
        final int startRow = structure.getStartRow().orElse(1);
        final int endRow = structure.getEndRow().orElse(rows);

        if (structure.getReplacement() == null) {
            throwConverterException("Replacement value must not be null.");
        }

        final UnaryOperator<String> mapper = getMapper();

        for (int i = startRow; i < endRow; i++) {
            for (Integer j : columnIndex) {
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
