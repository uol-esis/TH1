package de.uol.pgdoener.th1.application.converterchain.converter;

import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.RemoveInvalidRowsStructureDto;
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
        List<Integer> invalidRowIndices = findInvalidRowIndices(inputMatrix);

        if (invalidRowIndices.size() == inputMatrix.length) {
            log.debug("All rows are invalid");
            return super.handleRequest(inputMatrix);
        }

        if (invalidRowIndices.isEmpty()) {
            log.debug("No invalid rows found.");
            return super.handleRequest(inputMatrix);
        }

        String[][] cleanedMatrix = buildCleanMatrix(inputMatrix, invalidRowIndices);

        return super.handleRequest(cleanedMatrix);
    }

    /**
     * Finds all row indices that are considered invalid.
     */
    private List<Integer> findInvalidRowIndices(String[][] inputMatrix) {
        List<Integer> invalidIndices = new ArrayList<>();
        int threshold = getThreshold(inputMatrix[0].length, removeHeaderStructure);

        for (int i = 0; i < inputMatrix.length; i++) {
            String[] row = inputMatrix[i];
            long validElementCount = countValidElements(row);
            if (validElementCount <= threshold) {
                log.debug("Invalid row found at index {} with {} valid elements", i, validElementCount);
                invalidIndices.add(i);
            }
        }

        return invalidIndices;
    }

    /**
     * Determines the threshold for the minimum number of valid columns per row.
     * If the table has 2 or fewer columns, a lower default threshold (1) is used.
     * Otherwise, a default threshold of 2 is applied, unless a specific value is provided in the DTO.
     *
     * @param dto The DTO containing an optional threshold value.
     * @return The threshold to be used.
     */
    private int getThreshold(int tableWidth, RemoveInvalidRowsStructureDto dto) {
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

        List<String> validElements = removeHeaderStructure.getBlockList();
        if (validElements.isEmpty()) {
            return true;
        }

        return validElements.stream().noneMatch(entry::contains);
    }

    /**
     * Builds a new matrix without invalid rows.
     */
    private String[][] buildCleanMatrix(String[][] inputMatrix, List<Integer> invalidIndices) {
        int validRowCount = inputMatrix.length - invalidIndices.size();
        String[][] cleanedMatrix = new String[validRowCount][];
        int cleanedMatrixIndex = 0;

        for (int i = 0; i < inputMatrix.length; i++) {
            if (!invalidIndices.contains(i)) {
                cleanedMatrix[cleanedMatrixIndex++] = inputMatrix[i];
            }
        }

        return cleanedMatrix;
    }
}
