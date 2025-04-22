package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.SplitRowStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
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

        List<String[]> rows = new ArrayList<>();
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

        return rows.toArray(new String[0][]);
    }

}
