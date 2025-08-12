package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.application.dto.SplitCellStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static de.uol.pgdoener.th1.application.dto.SplitCellStructureDto.ModeEnum.ROW;

@Slf4j
@RequiredArgsConstructor
public class SplitCellConverter extends Converter {

    private final SplitCellStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        int columnIndex = structure.getColumnIndex();
        String delimiter = structure.getDelimiter().orElse("\n");
        Pattern delimiterPattern = Pattern.compile(delimiter);
        int startRow = structure.getStartRow().orElse(0);
        int endRow = structure.getEndRow().orElse(matrix.length);

        if (startRow < 0 || startRow > matrix.length) {
            throwConverterException("Invalid startRow index");
        }
        if (endRow < 0 || endRow > matrix.length) {
            throwConverterException("Invalid endRow index");
        }
        if (columnIndex < 0 || columnIndex >= matrix[startRow].length) {
            throwConverterException("Invalid column index");
        }

        return super.handleRequest(switch (structure.getMode().orElse(ROW)) {
            case ROW -> splitIntoRows(matrix, startRow, endRow, columnIndex, delimiterPattern);
            case COLUMN -> splitIntoColumns(matrix, startRow, endRow, columnIndex, delimiterPattern);
            case UNKNOWN_DEFAULT_OPEN_API ->
                    throw new IllegalArgumentException("Unknown mode " + structure.getMode().orElse(ROW) + " for SplitCellConverter");
        });
    }

    private String[][] splitIntoRows(String[][] matrix, int startRow, int endRow, int columnIndex, Pattern delimiterPattern) {
        List<String[]> rows;
        // Add rows before the split
        List<String[]> header = Arrays.asList(matrix).subList(0, startRow);
        rows = new ArrayList<>(header);

        // Split the specified column for each row in the specified range
        for (int i = startRow; i < endRow; i++) {
            String[] row = matrix[i];
            String multiValue = row[columnIndex];
            String[] splitValues = delimiterPattern.split(multiValue);
            for (String splitValue : splitValues) {
                String[] newRow = new String[row.length];
                System.arraycopy(row, 0, newRow, 0, row.length);
                newRow[columnIndex] = splitValue;
                rows.add(newRow);
            }
        }

        // Add rows after the split
        List<String[]> footer = Arrays.asList(matrix).subList(endRow, matrix.length);
        rows.addAll(footer);
        return rows.toArray(new String[0][]);
    }

    private String[][] splitIntoColumns(String[][] matrix, int startRow, int endRow, int columnIndex, Pattern delimiterPattern) {
        SplitResult splitResult = splitRowsIntoColumns(matrix, startRow, endRow, columnIndex, delimiterPattern);
        List<String[]> rows = splitResult.splitValues;
        int maxSplitValuesLength = splitResult.maxSplitValuesLength;
        String[][] newMatrix = new String[matrix.length][matrix[0].length + maxSplitValuesLength - 1];

        // Copy the header rows before the split
        for (int i = 0; i < startRow; i++) {
            newMatrix[i] = constructRowSplitIntoColumns(matrix[i], columnIndex, maxSplitValuesLength, matrix[i][columnIndex]);
        }
        // Process the rows that are being split
        for (int i = startRow; i < endRow; i++) {
            String[] oldRow = matrix[i];
            String[] splitValues = rows.get(i - startRow);
            newMatrix[i] = constructRowSplitIntoColumns(oldRow, columnIndex, maxSplitValuesLength, splitValues);
        }
        // Copy the footer rows after the split
        for (int i = endRow; i < matrix.length; i++) {
            newMatrix[i] = constructRowSplitIntoColumns(matrix[i], columnIndex, maxSplitValuesLength, matrix[i][columnIndex]);
        }

        return newMatrix;
    }

    /**
     * This method splits the specified column of the matrix into multiple columns based on the specified delimiter.
     * The returned list contains the columns of the matrix with the specified column split into multiple columns.
     * The columns before and after the specified column are not preserved in the returned list.
     */
    private SplitResult splitRowsIntoColumns(String[][] matrix, int startRow, int endRow, int columnIndex, Pattern delimiterPattern) {
        List<String[]> rows = new ArrayList<>(matrix.length);
        int maxSplitValuesLength = 0;

        for (int i = startRow; i < endRow; i++) {
            String[] row = matrix[i];
            String multiValue = row[columnIndex];
            String[] splitValues = delimiterPattern.split(multiValue);
            rows.add(splitValues);
            maxSplitValuesLength = Math.max(maxSplitValuesLength, splitValues.length);
        }

        return new SplitResult(
                rows,
                maxSplitValuesLength
        );
    }

    private record SplitResult(
            List<String[]> splitValues,
            int maxSplitValuesLength
    ) {
    }

    /**
     * Constructs a new row using the provided old row, column index, maximum split values length, and split values.
     * The new row will have the same content before the split column.
     * The split values will be inserted at the specified column index, and the remaining columns will be filled with empty strings.
     * The columns after the split column will be copied from the old row.
     */
    private String[] constructRowSplitIntoColumns(String[] oldRow, int columnIndex, int maxSplitValuesLength, String... splitValues) {
        String[] newRow = new String[oldRow.length + maxSplitValuesLength - 1];
        System.arraycopy(oldRow, 0, newRow, 0, columnIndex); // Copy the columns before the split
        System.arraycopy(splitValues, 0, newRow, columnIndex, splitValues.length); // Add the split values to the new row
        Arrays.fill(newRow, columnIndex + splitValues.length, columnIndex + maxSplitValuesLength, ""); // Fill the remaining columns with ""
        System.arraycopy(oldRow, columnIndex + 1, newRow, columnIndex + maxSplitValuesLength, oldRow.length - columnIndex - 1); // Copy the columns after the split
        return newRow;
    }

}
