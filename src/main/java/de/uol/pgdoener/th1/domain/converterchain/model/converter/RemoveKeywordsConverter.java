package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.RemoveKeywordsStructureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class RemoveKeywordsConverter extends Converter {

    private final RemoveKeywordsStructureDto structure;

    /**
     * Processes the input matrix by optionally removing columns and/or rows that match the configured keywords.
     *
     * @param inputMatrix the input string matrix to be filtered
     * @return a new matrix with removed columns and/or rows; unchanged if no removal is needed
     */
    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        String[][] resultMatrix = inputMatrix;

        Pattern keywordPattern = buildPattern();

        if (structure.isRemoveColumns()) {
            resultMatrix = removeColumnsWithHeaderKeywords(inputMatrix, keywordPattern);
        }

        if (structure.isRemoveRows()) {
            resultMatrix = removeRowsContainingKeywords(resultMatrix, keywordPattern);
        }

        return super.handleRequest(resultMatrix);
    }

    // ----------------- Private helper methods ----------------- //

    /**
     * Builds a regular expression Pattern based on the configured keywords, match type, and case sensitivity.
     * <p>
     * The pattern is used to efficiently match cell values against the given keyword list, supporting either
     * exact matches (EQUALS) or partial matches (CONTAINS). Keywords are properly escaped to avoid regex issues.
     * If no keywords are provided, a pattern is returned that never matches any string.
     *
     * @return a compiled Pattern representing the keyword matching logic
     */
    private Pattern buildPattern() {
        if (structure.getKeywords() == null || structure.getKeywords().isEmpty()) {
            return Pattern.compile("a^"); // never matches
        }
        List<String> escapedKeywords = new ArrayList<>();
        for (String kw : structure.getKeywords()) {
            if (kw != null) {
                escapedKeywords.add(Pattern.quote(kw));
            }
        }
        String joined = String.join("|", escapedKeywords);
        String regex = switch (structure.getMatchType()) {
            case RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS -> ".*(" + joined + ").*";
            case RemoveKeywordsStructureDto.MatchTypeEnum.EQUALS -> "^(" + joined + ")$";
            default -> {
                log.warn("Unknown matchType '{}', defaulting to CONTAINS", structure.getMatchType());
                yield ".*(" + joined + ").*";
            }
        };
        int flags = structure.isIgnoreCase() ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        return Pattern.compile(regex, flags);
    }

    /**
     * Removes all columns from the matrix whose header values match any of the configured keywords.
     * <p>
     * Only the first row (header) is used for comparison.
     *
     * @param matrix the original input matrix
     * @return a new matrix with columns removed where the header matches a keyword
     */
    private String[][] removeColumnsWithHeaderKeywords(String[][] matrix, Pattern keywordPattern) {
        int rowLength = matrix.length;
        int columnLength = matrix[0].length;

        List<Integer> columnsToRemove = identifyColumnsToRemove(matrix[0], keywordPattern);

        int newColumnLength = columnLength - columnsToRemove.size();
        String[][] newMatrix = new String[rowLength][newColumnLength];

        for (int rowIndex = 0; rowIndex < rowLength; rowIndex++) {
            int newColIndex = 0;
            for (int colIndex = 0; colIndex < columnLength; colIndex++) {
                if (!columnsToRemove.contains(colIndex)) {
                    String cellEntry = matrix[rowIndex][colIndex];
                    newMatrix[rowIndex][newColIndex++] = cellEntry;
                }
            }
        }

        return newMatrix;
    }

    /**
     * Identifies column indices in the header row that match any keyword.
     *
     * @param headerRow the first row of the matrix, assumed to be the header
     * @return a list of column indices that should be removed
     */
    private List<Integer> identifyColumnsToRemove(String[] headerRow, Pattern keywordPattern) {
        List<Integer> columnsToRemove = new ArrayList<>();

        for (int col = 0; col < headerRow.length; col++) {
            String cell = headerRow[col];
            if (matchesKeyword(cell, keywordPattern)) {
                columnsToRemove.add(col);
            }
        }

        return columnsToRemove;
    }

    /**
     * Removes all rows (excluding the header row) that contain at least one cell matching any keyword.
     *
     * @param matrix the input matrix to be filtered
     * @return a new matrix with rows removed where any cell matches a keyword; header row is always retained
     */
    private String[][] removeRowsContainingKeywords(String[][] matrix, Pattern keywordPattern) {
        List<String[]> filteredRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            if (i == 0) {
                filteredRows.add(matrix[i]);
                continue;
            }

            if (!rowContainsKeyword(matrix[i], keywordPattern)) {
                filteredRows.add(matrix[i]);
            }
        }

        return filteredRows.toArray(new String[0][]);
    }

    /**
     * Checks whether a given row contains any keyword match in any of its cells.
     *
     * @param row the row to be checked
     * @return true if any cell matches a keyword, false otherwise
     */
    private boolean rowContainsKeyword(String[] row, Pattern keywordPattern) {
        for (String cell : row) {
            if (matchesKeyword(cell, keywordPattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given string value matches any of the configured keywords.
     * <p>
     * Matching is performed according to the structure's {@code ignoreCase} and {@code matchType} settings.
     *
     * @param value the cell value to check
     * @return true if the value matches a keyword, false otherwise
     */
    private boolean matchesKeyword(String value, Pattern keywordPattern) {
        if (value == null) return false;
        String cellValue = structure.isIgnoreCase() ? value.toLowerCase() : value;
        return keywordPattern.matcher(cellValue).matches();
    }
}