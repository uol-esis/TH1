package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveFooterStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RemoveFooterConverter extends Converter {

    private final RemoveFooterStructureDto removeFooterStructure;

    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        Integer lastValidRowIndex = null;
        int threshold = getThreshold(inputMatrix[0].length, removeFooterStructure);

        // Find the last line with valid elements
        for (int i = inputMatrix.length - 1; i >= 0; i--) {
            String[] row = inputMatrix[i];
            long validElementCount = countValidElements(row);

            if (validElementCount > threshold) {
                log.debug("Find LasValidRow at id {} with {} valid elements", i, validElementCount);
                lastValidRowIndex = i;
                break;
            }
        }

        if (lastValidRowIndex == null) {
            log.debug("No Footer to remove found");
            return super.handleRequest(inputMatrix);
        }

        // Remove all lines up to and including the header line
        int rowsToKeep = lastValidRowIndex + 1;
        String[][] cleanedMatrix = new String[rowsToKeep][];
        System.arraycopy(inputMatrix, 0, cleanedMatrix, 0, rowsToKeep);

        return super.handleRequest(cleanedMatrix);
    }

    // private Methods //

    /**
     * Determines the threshold for the minimum number of valid columns per row.
     * If the table has 2 or fewer columns, a lower default threshold (1) is used.
     * Otherwise, a default threshold of 2 is applied, unless a specific value is provided in the DTO.
     *
     * @param dto The DTO containing an optional threshold value.
     * @return The threshold to be used.
     */
    private int getThreshold(int tableWidth, RemoveFooterStructureDto dto) {
        int defaultThreshold = (tableWidth <= 2) ? 1 : 2;
        return dto.getThreshold().orElse(defaultThreshold);
    }

    /**
     * Counts the number of valid elements in a row.
     * Valid = not null, not empty, not equal to "*"
     */
    private long countValidElements(String[] row) {
        return Arrays.stream(row)
                .filter(this::isValidEntry)
                .count();
    }

    /**
     * Returns true if the entry is considered invalid.
     * Invalid = null, empty string, or a literal "*"
     */
    private boolean isValidEntry(String entry) {
        if (entry == null || entry.isBlank()) {
            return false;
        }

        List<String> validElements = removeFooterStructure.getBlockList();
        if (validElements.isEmpty()) {
            return true;
        }

        return validElements.stream().noneMatch(entry::contains);
    }
}
