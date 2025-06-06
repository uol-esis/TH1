package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveLeadingColumnStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RemoveLeadingColumnConverter extends Converter {

    private final RemoveLeadingColumnStructureDto structure;

    /**
     * Processes a 2D String array by removing leading columns that contain invalid entries.
     * A valid entry is one that is not null, not empty, and not in the block list (e.g. "*").
     * <p>
     * It finds the starting column index from which valid data begins across all rows,
     * then creates a trimmed matrix by removing all columns before that index.
     * <p>
     * If no valid entries are found, the original matrix is returned.
     *
     * @param inputMatrix input 2D array to process
     * @return processed 2D array with leading invalid columns removed, or original matrix if none found
     */
    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        int columnStartIndex = findColumnStartIndex(inputMatrix);

        if (columnStartIndex == inputMatrix[0].length) {
            log.debug("No valid leading columns found — returning original matrix");
            return super.handleRequest(inputMatrix);
        }

        String[][] cleanedMatrix = createCleanedMatrix(inputMatrix, columnStartIndex);
        return super.handleRequest(cleanedMatrix);
    }

    /**
     * Determines the maximum starting column index across all rows from which valid entries begin.
     *
     * @param inputMatrix the input 2D array
     * @return the index of the first valid column considering all rows
     */
    private int findColumnStartIndex(String[][] inputMatrix) {
        int columnStartIndex = inputMatrix[0].length;
        for (String[] row : inputMatrix) {
            int validStartIndex = findValidStartIndex(row);

            if (validStartIndex < columnStartIndex) {
                columnStartIndex = validStartIndex;
            }

            if (columnStartIndex == row.length) {
                log.debug("No column trailing found — returning original matrix");
                break;
            }
        }
        log.debug("Max valid element count in the matrix: {}", columnStartIndex);
        return columnStartIndex;
    }

    /**
     * Finds the index of the first valid entry in a single row.
     *
     * @param row the array representing a row of entries
     * @return index of the first valid entry, or row.length if none found
     */
    private int findValidStartIndex(String[] row) {
        for (int i = 0; i < row.length; i++) {
            if (isValidEntry(row[i])) {
                return i;
            }
        }
        return row.length;
    }

    /**
     * Checks whether a given entry is valid.
     * Valid means the entry is not null, not blank, and does not contain any blocked strings.
     *
     * @param entry the string entry to check
     * @return true if valid, false otherwise
     */
    private boolean isValidEntry(String entry) {
        if (entry == null || entry.isBlank()) {
            return false;
        }

        List<String> validElements = structure.getBlockList();
        if (validElements.isEmpty()) {
            return true;
        }

        return validElements.stream().noneMatch(entry::equals);
    }

    /**
     * Creates a new matrix by removing the leading columns before the given start index.
     *
     * @param inputMatrix      the original 2D array
     * @param columnStartIndex the column index to start copying from (inclusive)
     * @return new 2D array with leading columns removed
     */
    private String[][] createCleanedMatrix(String[][] inputMatrix, int columnStartIndex) {
        return Arrays.stream(inputMatrix)
                .map(row -> Arrays.copyOfRange(row, columnStartIndex, row.length))
                .toArray(String[][]::new);
    }

}
