package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveColumnByIndexStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class RemoveColumnByIndexConverter extends Converter {

    private final RemoveColumnByIndexStructureDto structure;

    @Override
    public String[][] handleRequest(String[][] matrix) {
        List<Integer> columnsToDelete = structure.getColumnIndex();

        int totalColumns = matrix[0].length;

        // Filter duplicates and out of bounds
        Set<Integer> deleteSet = new HashSet<>();
        for (int col : columnsToDelete) {
            if (col < 0 || col >= totalColumns) {
                throwConverterException("Index " + col + " out of bounds for matrix with " + totalColumns + " columns");
            } else {
                deleteSet.add(col);
            }
        }

        int newColumnCount = totalColumns - deleteSet.size();

        // Create new matrix that contains the columns without the ones to be deleted
        String[][] newMatrix = new String[matrix.length][newColumnCount];
        for (int i = 0; i < matrix.length; i++) {
            int newColIndex = 0;
            for (int j = 0; j < totalColumns; j++) {
                if (!deleteSet.contains(j)) {
                    newMatrix[i][newColIndex++] = matrix[i][j];
                }
            }
        }
        return super.handleRequest(newMatrix);
    }
}
