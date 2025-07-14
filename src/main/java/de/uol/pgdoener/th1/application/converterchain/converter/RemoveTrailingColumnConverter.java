package de.uol.pgdoener.th1.application.converterchain.converter;

import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.RemoveTrailingColumnStructureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RemoveTrailingColumnConverter extends Converter {

    private final RemoveTrailingColumnStructureDto structure;

    /**
     * Processes a 2D String array by removing trailing columns that contain invalid entries.
     * A valid entry is one that is not null, not empty, and not in the block list (e.g. "*").
     * <p>
     * It finds the maximum valid column length across all rows,
     * then creates a trimmed matrix by cutting off all trailing invalid columns.
     * <p>
     * If no valid entries are found, the original matrix is returned.
     *
     * @param inputMatrix input 2D array to process
     * @return processed 2D array with trailing invalid columns removed, or original matrix if none found
     */
    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        int maxValidRowLength = findMaxValidRowLength(inputMatrix);

        if (maxValidRowLength == 0) {
            log.debug("No valid trailing columns found — returning original matrix");
            return super.handleRequest(inputMatrix);
        }

        String[][] cleanedMatrix = createCleanedMatrix(inputMatrix, maxValidRowLength);
        return super.handleRequest(cleanedMatrix);
    }

    /**
     * Determines the maximum number of valid columns found at the end of any row.
     *
     * @param inputMatrix the input 2D array
     * @return the maximum number of valid columns across all rows
     */
    private int findMaxValidRowLength(String[][] inputMatrix) {
        int maxValidRowLength = 0;
        for (String[] row : inputMatrix) {
            int validRowLength = findValidRowLength(row);

            if (validRowLength > maxValidRowLength) {
                maxValidRowLength = validRowLength;
            }

            if (maxValidRowLength == row.length) {
                break;
            }
        }
        log.debug("Max valid element count in the matrix: {}", maxValidRowLength);
        return maxValidRowLength;
    }

    /**
     * Determines the number of valid elements from the beginning up to the last valid index.
     *
     * @param row the array representing a row of entries
     * @return number of valid elements (excluding trailing invalid entries)
     */
    private int findValidRowLength(String[] row) {
        int validRowLength = row.length;
        for (int i = row.length - 1; i >= 0; i--) {
            if (isValidEntry(row[i])) {
                return validRowLength;
            }
            validRowLength--;
        }

        return validRowLength;
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
     * Creates a new matrix by removing trailing columns beyond the given column end index.
     *
     * @param inputMatrix    the original 2D array
     * @param columnEndIndex the column index up to which data should be kept (exclusive)
     * @return new 2D array with trailing columns removed
     */
    private String[][] createCleanedMatrix(String[][] inputMatrix, int columnEndIndex) {
        return Arrays.stream(inputMatrix)
                .map(row -> Arrays.copyOfRange(row, 0, columnEndIndex))
                .toArray(String[][]::new);
    }
}
