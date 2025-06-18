package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveHeaderStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RemoveHeaderConverter extends Converter {
    private final RemoveHeaderStructureDto removeHeaderStructure;

    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        Integer headerRowIndex = null;
        int threshold = removeHeaderStructure.getThreshold().orElse(2);

        // Find the first line with at least two valid elements
        for (int i = 0; i < inputMatrix.length; i++) {
            String[] row = inputMatrix[i];
            long validElementCount = countValidElements(row);

            if (validElementCount > threshold) {
                log.debug("Find HeaderRow at id {} with {} valid elements", i, validElementCount);
                headerRowIndex = i;
                break;
            }
        }

        if (headerRowIndex == null) {
            log.debug("No HeaderRow found");
            return super.handleRequest(inputMatrix);
        }

        // Remove all lines up to and including the header line
        int rowsToKeep = inputMatrix.length - headerRowIndex;
        String[][] cleanedMatrix = new String[rowsToKeep][];
        System.arraycopy(inputMatrix, headerRowIndex, cleanedMatrix, 0, rowsToKeep);

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
        if (entry == null || entry.isBlank()) {
            return false;
        }

        List<String> validElements = removeHeaderStructure.getBlockList();
        if (validElements.isEmpty()) {
            return true;
        }

        return validElements.stream().noneMatch(entry::contains);
    }
}
