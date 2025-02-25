package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveColumnByIndexStructure;

import java.util.HashSet;
import java.util.Set;

public class RemoveColumnByIndexConverter extends Converter {
    private final RemoveColumnByIndexStructure structure;

    public RemoveColumnByIndexConverter(RemoveColumnByIndexStructure structure) {
        this.structure = structure;
    }

    @Override
    public String[][] handleRequest(String[][] matrix) {
        Integer[] columnsToDelete = structure.columns();

        int totalColumns = matrix[0].length;

        Set<Integer> deleteSet = new HashSet<>();
        for (int col : columnsToDelete) {
            if (col >= 0 && col < totalColumns) {
                deleteSet.add(col);
            }
        }

        int newColumnCount = totalColumns - deleteSet.size();

        // Neue Matrix erstellen
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
