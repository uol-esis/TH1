package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveTrailingColumnStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RemoveTrailingColumnConverter extends Converter {

    private final RemoveTrailingColumnStructureDto structure;

    /**
     * Handles a request to process a 2D array of strings, cleaning up each row
     * by trimming it to only contain valid elements. A valid element is defined
     * as:
     * - Not null,
     * - Not empty,
     * - Not equal to "*".
     * <p>
     * This method first finds the maximum count of valid elements in any row
     * of the input matrix using the {@link #findMaxValidRowLength} method.
     * It then creates a new cleaned matrix, where each row is truncated to
     * the maximum count of valid elements.
     * If no valid elements are found (i.e., maxValidElementCount == 0),
     * the original matrix is returned.
     *
     * @param inputMatrix A 2D array (String[][]) that contains the input matrix
     *                    to be processed. The matrix's rows will be cleaned
     *                    based on valid element counts.
     * @return A new 2D array (String[][]) where each row is truncated to the
     * maximum count of valid elements found in any row. If no valid
     * elements are found, the original matrix is returned.
     */
    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        int maxValidRowLength = findMaxValidRowLength(inputMatrix);

        if (maxValidRowLength == 0) {
            log.debug("No valid elements");
            return super.handleRequest(inputMatrix);
        }

        log.debug("Max valid element count found: {}", maxValidRowLength);

        String[][] cleanedMatrix = new String[inputMatrix.length][];

        for (int i = 0; i < inputMatrix.length; i++) {
            String[] cleanedRow = new String[maxValidRowLength];
            System.arraycopy(inputMatrix[i], 0, cleanedRow, 0, maxValidRowLength);
            cleanedMatrix[i] = cleanedRow;
            log.debug("Processed row {}: {}", i, Arrays.toString(cleanedRow));
        }
        return super.handleRequest(cleanedMatrix);
    }

    /**
     * Finds the maximum number of valid elements in any row of a 2D array.
     * A valid element is defined as:
     * - Not null,
     * - Not empty,
     * - Not equal to "*".
     * <p>
     * This method iterates through each row of the 2D array, counting the valid elements.
     * It returns the maximum count of valid elements found in any single row.
     * The iteration stops early if a row's valid element count equals the number of elements in that row.
     *
     * @param inputMatrix A 2D array (String[][]) where each row is examined for valid elements.
     *                    Each row is checked individually for valid entries.
     * @return The maximum number of valid elements found in any row of the input array.
     */
    private int findMaxValidRowLength(String[][] inputMatrix) {
        int maxValidRowLength = 0;
        for (int i = 0; i < inputMatrix.length; i++) {
            String[] row = inputMatrix[i];
            int validRowLength = getValidRowLength(row);

            log.debug("Row {} has {} valid elements.", i, validRowLength);

            if (validRowLength > maxValidRowLength) {
                log.debug("Find maxRowLength at id {} with {} valid elements", i, validRowLength);
                maxValidRowLength = validRowLength;
            }

            if (maxValidRowLength == row.length) {
                log.debug("No column trailing found");
                break;
            }
        }
        log.info("Max valid element count in the matrix: {}", maxValidRowLength);
        return maxValidRowLength;
    }

    /**
     * Counts the number of valid elements in a row.
     * A valid element is defined as one that is neither null, empty, nor a literal "*".
     *
     * @param row The row of elements to be checked.
     * @return The count of valid elements in the row.
     */
    private int getValidRowLength(String[] row) {
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
     * Determines whether an entry is valid.
     * A valid element is defined as:
     * - Not null,
     * - Not empty,
     * - Not equal to "*".
     * <p>
     * It can also be further checked against a blacklist, where any entry matching
     * an element in the blacklist is considered invalid.
     *
     * @param entry The entry to be checked.
     * @return true if the entry is valid, false otherwise.
     */
    private boolean isValidEntry(String entry) {
        if (entry == null || entry.isBlank()) {
            log.debug("Entry is invalid (null or blank).");
            return false;
        }

        List<String> validElements = structure.getBlackList();
        if (validElements.isEmpty()) {
            log.debug("No blacklist defined, entry '{}' is valid.", entry);
            return true;
        }

        return validElements.stream().noneMatch(entry::contains);
    }
}
