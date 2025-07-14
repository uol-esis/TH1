package de.uol.pgdoener.th1.application.converterchain.converter;

import de.uol.pgdoener.th1.application.converterchain.model.Converter;
import de.uol.pgdoener.th1.application.dto.RemoveRowByIndexStructureDto;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class RemoveRowByIndexConverter extends Converter {

    private final RemoveRowByIndexStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        List<Integer> rowsToDelete = structure.getRowIndex();

        int totalRows = matrix.length;

        // Filter duplicates and out of bounds
        Set<Integer> deleteSet = new HashSet<>();
        for (int row : rowsToDelete) {
            if (row < 0 || row >= totalRows) {
                throwConverterException("Index " + row + " out of bounds for matrix with " + totalRows + " rows");
            } else {
                deleteSet.add(row);
            }
        }

        // Create new matrix that contains the rows without the ones to be deleted
        String[][] newMatrix = new String[matrix.length - deleteSet.size()][matrix[0].length];
        int newRowIndex = 0;

        for (int i = 0; i < matrix.length; i++) {
            if (!deleteSet.contains(i)) {
                newMatrix[newRowIndex++] = matrix[i];
            }
        }

        return super.handleRequest(newMatrix);
    }
}
