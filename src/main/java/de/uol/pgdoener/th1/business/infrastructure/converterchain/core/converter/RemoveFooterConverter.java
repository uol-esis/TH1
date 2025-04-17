package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveFooterStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class RemoveFooterConverter extends Converter {
    private final RemoveFooterStructure removeFooterStructure;

    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        Integer lastValidRowIndex = null;
        int threshold = removeFooterStructure.threshold() == null ? 2 : removeFooterStructure.threshold();

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
            return inputMatrix;
        }

        // Remove all lines up to and including the header line
        int rowsToKeep = lastValidRowIndex + 1;
        String[][] cleanedMatrix = new String[rowsToKeep][];
        System.arraycopy(inputMatrix, 0, cleanedMatrix, 0, rowsToKeep);

        return super.handleRequest(cleanedMatrix);
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
        if (entry == null || entry.trim().isEmpty()) {
            return false;
        }

        String[] validElements = removeFooterStructure.blackList();
        if (ArrayUtils.isEmpty(validElements)) {
            return true;
        }

        return Arrays.stream(validElements).noneMatch(entry::contains);
    }
}
