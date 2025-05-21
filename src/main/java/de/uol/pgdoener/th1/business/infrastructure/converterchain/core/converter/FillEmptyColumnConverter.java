package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.FillEmptyColumnStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
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
            for (int i = 0; i < matrix.length; i++) {
                if (!matrix[i][columnIndex].isBlank()) {
                    lastNonEmptyValue = matrix[i][columnIndex];
                } else {
                    if (lastNonEmptyValue.isBlank()) {
                        throwConverterException("No non-empty value found in the column to fill empty cells");
                    }
                    matrix[i][columnIndex] = lastNonEmptyValue;
                }
            }
        }
        return super.handleRequest(matrix);
    }
}
