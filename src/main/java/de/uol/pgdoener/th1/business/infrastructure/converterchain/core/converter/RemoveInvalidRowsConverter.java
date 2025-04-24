package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveInvalidRowsStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RemoveInvalidRowsConverter extends Converter {

    private final RemoveInvalidRowsStructureDto removeHeaderStructure;

    @Override
    public String[][] handleRequest(String[][] inputMatrix) {
        List<Integer> removeIndexes = new ArrayList<>();
        int threshold = removeHeaderStructure.getThreshold().orElse(2);

        log.debug(Arrays.toString(inputMatrix));

        // Find the first line with at least two valid elements
        for (int i = 0; i < inputMatrix.length; i++) {
            String[] row = inputMatrix[i];
            long validElementCount = countValidElements(row);

            if (validElementCount <= threshold) {
                log.debug("Find invalid row at id {} with {} valid elements", i, validElementCount);
                removeIndexes.add(i);
            }
        }

        if (removeIndexes.isEmpty()) {
            log.debug("No HeaderRow found");
            return inputMatrix;
        }

        int rowsToKeep = inputMatrix.length - removeIndexes.size();
        String[][] cleanedMatrix = new String[rowsToKeep][];
        int cleanedMatrixIndex = 0;

        for (int i = 0; i < inputMatrix.length; i++) {

            if (removeIndexes.contains(i)) {
                continue;
            }

            cleanedMatrix[cleanedMatrixIndex] = inputMatrix[i];
            cleanedMatrixIndex++;
        }

        if (Arrays.stream(cleanedMatrix).findAny().isEmpty()) {
            log.warn("Cleaned matrix is null – returning original inputMatrix");
            return inputMatrix;
        }

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

        List<String> validElements = removeHeaderStructure.getBlackList();
        if (validElements.isEmpty()) {
            return true;
        }

        return validElements.stream().noneMatch(entry::contains);
    }
}
