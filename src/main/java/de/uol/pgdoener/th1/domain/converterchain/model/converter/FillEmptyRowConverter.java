package de.uol.pgdoener.th1.domain.converterchain.model.converter;

import de.uol.pgdoener.th1.domain.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.FillEmptyRowStructureDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FillEmptyRowConverter extends Converter {

    private final FillEmptyRowStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        List<Integer> rowsToFill = structure.getRowIndex();

        for (Integer row : rowsToFill) {
            if (row < 0 || row >= matrix.length) {
                throwConverterException("Index " + row + " out of bounds for matrix with " + matrix.length + " rows");
            }
        }

        for (int rowIndex : rowsToFill) {
            String[] row = matrix[rowIndex];
            String lastNonEmptyValue = "";
            for (int i = 0; i < row.length; i++) {
                if (!row[i].isBlank()) {
                    lastNonEmptyValue = row[i];
                } else {
                    if (lastNonEmptyValue.isBlank()) {
                        continue;
                    }
                    row[i] = lastNonEmptyValue;
                }
            }
        }
        return super.handleRequest(matrix);
    }
}
