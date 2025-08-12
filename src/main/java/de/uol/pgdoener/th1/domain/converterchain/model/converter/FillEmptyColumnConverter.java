package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.FillEmptyColumnStructureDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FillEmptyColumnConverter extends Converter {

    private final FillEmptyColumnStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        List<Integer> columnsToFill = structure.getColumnIndex();

        for (Integer column : columnsToFill) {
            if (column < 0 || column >= matrix[0].length) {
                throwConverterException("Index " + column + " out of bounds for matrix with " + matrix[0].length + " columns");
            }
        }

        for (int columnIndex : columnsToFill) {
            String lastNonEmptyValue = "";
            for (String[] row : matrix) {
                if (!row[columnIndex].isBlank()) {
                    lastNonEmptyValue = row[columnIndex];
                } else {
                    row[columnIndex] = lastNonEmptyValue;
                }
            }
            if (lastNonEmptyValue.isBlank()) {
                throwConverterException("No non-empty value found in the column to fill empty cells");
            }
        }
        return super.handleRequest(matrix);
    }
}
