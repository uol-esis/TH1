package de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.csv_converter.core.structures.RemoveRowByIndexStructure;

public class RemoveRowByIndexConverter extends Converter {

    private final RemoveRowByIndexStructure structure;

    public RemoveRowByIndexConverter(RemoveRowByIndexStructure structure) {
        this.structure = structure;
    }

    @Override
    public String[][] handleRequest(String[][] matrix) {
        Integer[] rowsToDelete = structure.rows(); // Annahme: "rows" enthält die Zeilenindizes, die gelöscht werden sollen

        // Neue Matrix erstellen, die die Zeilen ohne die zu löschenden enthält
        String[][] newMatrix = new String[matrix.length - rowsToDelete.length][matrix[0].length];
        int newRowIndex = 0;

        for (int i = 0; i < matrix.length; i++) {
            // Wenn die Zeile nicht zum Löschen gehört, kopiere sie in die neue Matrix
            boolean isRowToDelete = false;
            for (int rowIndex : rowsToDelete) {
                if (i == rowIndex) {
                    isRowToDelete = true;
                    break;
                }
            }

            if (!isRowToDelete) {
                newMatrix[newRowIndex++] = matrix[i];
            }
        }
        return super.handleRequest(newMatrix);
    }
}
