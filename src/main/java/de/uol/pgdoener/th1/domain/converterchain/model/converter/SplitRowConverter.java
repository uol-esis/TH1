package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.SplitRowStructureDto;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class SplitRowConverter extends Converter {

    private final SplitRowStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        int columnIndex = structure.getColumnIndex();
        String delimiter = structure.getDelimiter().orElse("\n");
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

        List<String[]> rows;
        // Add rows before the split
        List<String[]> header = Arrays.asList(matrix).subList(0, startRow);
        rows = new ArrayList<>(header);

        // Split the specified column for each row in the specified range
        for (int i = startRow; i < endRow; i++) {
            String[] row = matrix[i];
            String multiValue = row[columnIndex];
            String[] splitValues = multiValue.split(delimiter);
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
        String[][] newMatrix = rows.toArray(new String[0][]);
        return super.handleRequest(newMatrix);
    }
}
