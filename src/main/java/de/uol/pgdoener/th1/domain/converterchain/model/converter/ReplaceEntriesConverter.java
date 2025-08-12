package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.application.dto.ReplaceEntriesStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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
        final Optional<String> search = structure.getSearch();
        final Optional<String> regexSearch = structure.getRegexSearch();

        if (search.isPresent()) {
            final String s = search.get();
            final String replacement = structure.getReplacement();
            mapper = value -> value.equals(s) ? replacement : value;
        } else if (regexSearch.isPresent()) {
            final String regex = regexSearch.get();
            final String replacement = structure.getReplacement();
            final Pattern pattern = Pattern.compile(regex);
            mapper = value -> pattern.matcher(value).matches() ? replacement : value;
        } else {
            throwConverterException("Either search or regexSearch must be provided.");
        }
        return mapper;
    }

}
