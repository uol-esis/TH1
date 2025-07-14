package de.uol.pgdoener.th1.application.converterchain.converter;

import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.PivotMatrixStructureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class PivotMatrixConverter extends Converter {

    private final PivotMatrixStructureDto structure;

    /**
     * Transforms a wide matrix into a long format matrix by pivoting columns into rows.
     * <p>
     *
     * @param matrix input 2D String array representing the data
     * @return transformed 2D String array after pivoting
     */
    @Override
    public String[][] handleRequest(String[][] matrix) {

        // 1. Validate if Map contains more than two empty list
        validateOnlyOneEmptyList();

        // 2. Fill the empty list in map automatically
        Set<Integer> usedIndices = getUsedIndices();
        Set<Integer> expectedIndices = getExpectedIndices(matrix[0].length);
        fillEmptyListWithRemainingIndices(usedIndices, expectedIndices);

        // 3. get used indices after fill empty list
        Set<Integer> valueIndices = getUsedIndices();
        // 4. validate for missing indices
        validateIndexCoverage(valueIndices, expectedIndices);

        // 5. Compute size for output matrix
        int totalRows = (matrix.length - 1) * valueIndices.size();
        int totalCols = structure.getBlockIndices().size() + structure.getPivotField().size() + 1;
        // 6. Create matrix
        String[][] result = new String[totalRows + 1][totalCols];

        // 7. write header
        writePivotHeaderRow(matrix, structure.getPivotField(), result[0]);
        // 8. write data
        writeDataRows(matrix, structure.getPivotField(), valueIndices, result);

        return super.handleRequest(result);
    }

    // ----------------- Private helper methods ----------------- //

    /**
     * Ensures only one category in the mapping is allowed to have an empty list of column indices.
     * Throws an exception if more than one is empty.
     */
    private void validateOnlyOneEmptyList() {
        long emptyListCount = structure.getPivotField().values().stream().filter(List::isEmpty).count();

        if (emptyListCount > 1) {
            throw new IllegalStateException("Only one category may have an empty column list. Found: " + emptyListCount);
        }
    }

    /**
     * Finds the single empty list in the map and fills it with the remaining indices
     * that are not used by other map entries or in the blockIndices.
     *
     * @param usedIndices     the set of indices collected from the mapping
     * @param expectedIndices the set of all expected indices (e.g. 1 to N excluding block indices)
     */
    private void fillEmptyListWithRemainingIndices(Set<Integer> usedIndices, Set<Integer> expectedIndices) {
        String keyToFill = findKeyToFill();
        if (keyToFill == null) return;

        Set<Integer> remaining = new TreeSet<>(expectedIndices);
        remaining.removeAll(usedIndices);

        structure.getPivotField().put(keyToFill, new ArrayList<>(remaining));
    }

    /**
     * Returns a set of all column indices used either in the fixed block columns or in the category mappings.
     *
     * @return set of all used column indices
     */
    private Set<Integer> getUsedIndices() {
        Set<Integer> usedIndices = new HashSet<>();
        structure.getPivotField().values().forEach(usedIndices::addAll);
        return usedIndices;
    }

    /**
     * Computes the set of column indices that should be covered in the map.
     * This includes all column indices except those in blockIndices.
     *
     * @param totalColumns total number of columns in the matrix
     * @return set of expected column indices
     */
    private Set<Integer> getExpectedIndices(int totalColumns) {
        Set<Integer> expected = new HashSet<>();
        for (int i = 0; i < totalColumns; i++) {
            if (!structure.getBlockIndices().contains(i)) {
                expected.add(i);
            }
        }
        return expected;
    }

    /**
     * Validates that the set of actual mapped indices exactly matches the expected set.
     * This ensures that all required columns (excluding block columns) are covered
     * by the mapping and that there are no missing or unexpected indices.
     *
     * @param actual   the set of indices collected from the mapping
     * @param expected the set of all expected indices (e.g. 1 to N excluding block indices)
     * @throws IllegalStateException if the actual indices do not match the expected set
     */
    private void validateIndexCoverage(Set<Integer> actual, Set<Integer> expected) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException("Mapped indices do not fully match expected columns.\n"
                    + "Expected: " + expected + "\n"
                    + "Actual:   " + actual);
        }
    }

    private String findKeyToFill() {
        String keyToFill = null;
        for (Map.Entry<String, List<Integer>> entry : structure.getPivotField().entrySet()) {
            if (entry.getValue().isEmpty()) {
                keyToFill = entry.getKey();
                break;
            }
        }
        return keyToFill;
    }

    /**
     * Builds the header row of the output matrix.
     * Adds block column names, category names (from keys), and a final "Wert" column.
     *
     * @param matrix      Original matrix (header is first row).
     * @param resolvedMap Resolved map of category column indices.
     * @param headerRow   The first row of the result matrix to be populated.
     */
    private void writePivotHeaderRow(String[][] matrix, Map<String, List<Integer>> resolvedMap, String[] headerRow) {
        int col = 0;

        for (int idx : structure.getBlockIndices()) {
            headerRow[col++] = matrix[0][idx];
        }

        for (String category : resolvedMap.keySet()) {
            headerRow[col++] = category;
        }

        headerRow[col] = "Wert";
    }

    /**
     * Transforms all data rows (except the header) into long format rows in the result matrix.
     * For each data cell (value index), a new row is created with corresponding category values.
     * If carry-forward is enabled for a category, missing values are filled with the last seen one.
     *
     * @param matrix       Input matrix (original format).
     * @param resolvedMap  Map of categories to their associated columns.
     * @param valueIndices Set of all relevant column indices to pivot.
     * @param result       Pre-allocated result matrix where rows are written.
     */
    private void writeDataRows(
            String[][] matrix, Map<String, List<Integer>> resolvedMap, Set<Integer> valueIndices,
            String[][] result
    ) {
        int resultRow = 1;
        for (int row = 1; row < matrix.length; row++) {

            // Flag for last values per map key
            Map<String, String> lastSeenValue = new HashMap<>();

            for (Integer valueIdx : valueIndices) {
                String[] newRow = new String[result[0].length];
                int col = 0;

                for (int idx : structure.getBlockIndices()) {
                    newRow[col++] = matrix[row][idx];
                }

                for (Map.Entry<String, List<Integer>> entry : resolvedMap.entrySet()) {
                    String key = entry.getKey();
                    List<Integer> indices = entry.getValue();

                    boolean valueBelongsToCategory = indices.contains(valueIdx);
                    boolean categoryCarriesForward = structure.getKeysToCarryForward().contains(key);

                    if (valueBelongsToCategory) {
                        // Insert current header value
                        String headerValue = matrix[0][valueIdx];
                        newRow[col++] = headerValue;

                        if (categoryCarriesForward) {
                            lastSeenValue.put(key, headerValue);
                        }

                    } else {
                        // Accept value from previous line or leave empty
                        String fallbackValue = categoryCarriesForward
                                ? lastSeenValue.getOrDefault(key, "")
                                : "";
                        newRow[col++] = fallbackValue;
                    }
                }
                newRow[col] = matrix[row][valueIdx];
                result[resultRow++] = newRow;
            }
        }
    }
}