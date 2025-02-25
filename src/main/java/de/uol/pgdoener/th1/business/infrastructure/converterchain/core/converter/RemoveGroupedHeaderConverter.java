package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveGroupedHeaderStructure;

//TODO: Bedingung hinzufügen? wann der converter genutzt werden kann.Z.B. am Ende vorher muss immer laufen oder muss vor .. laufen usw.
public class RemoveGroupedHeaderConverter extends Converter {
    private final RemoveGroupedHeaderStructure structure;

    public RemoveGroupedHeaderConverter(RemoveGroupedHeaderStructure structure) {
        this.structure = structure;
    }

    @Override
    public String[][] handleRequest(String[][] matrix) {
        Integer[] rows = structure.rows();
        Integer[] columns = structure.columns();
        int startRow = structure.startRow();
        int startColumn = structure.startColumn();
        int endRow = structure.endRow();
        int endColumn = structure.endColumn();

        String[][] rowValues = new String[rows.length][structure.endRow()];
        for (int rowI : rows) {
            rowValues[rowI] = matrix[rowI];
        }

        int resultRows = (endRow - startRow) * (endColumn - startColumn);
        String[][] transformedMatrix = new String[resultRows + 1][];
        int index = 0;

        //TODO: Header überlegen auszulagern. Eigener Converter? Gehört aber auch irgednwie zum header auflösen dazu

        // Add header row dynamically based on grouped headers
        String[] headerRow = new String[rows.length + columns.length + 1];
        int headerIndex = 0;

        // Add column group headers (e.g., "Alter der Person")
        for (int colI : columns) {
            headerRow[headerIndex++] = matrix[rows.length][0]; // Assume first row contains column headers
        }

        // Add row group headers (e.g., "Geschlecht")
        for (int rowI : rows) {
            headerRow[headerIndex++] = matrix[rowI][0]; // Assume first column contains group headers
        }

        headerRow[headerIndex] = "Anzahl";
        transformedMatrix[index++] = headerRow;

        for (int i = startRow; i < endRow; i++) {
            String[] row = matrix[i];
            for (int j = startColumn; j < endColumn; j++) {
                String[] newRow = new String[rows.length + columns.length + 1];
                int newRowIndex = 0;

                // Spaltenwerte hinzufügen
                for (int colI : columns) {
                    newRow[newRowIndex++] = row[colI];
                }
                // Zeilenwerte hinzufügen
                for (String[] values : rowValues) {
                    newRow[newRowIndex++] = values[j];
                }
                newRow[newRowIndex] = row[j];
                transformedMatrix[index++] = newRow;
            }
        }
        return super.handleRequest(transformedMatrix);
    }
}
