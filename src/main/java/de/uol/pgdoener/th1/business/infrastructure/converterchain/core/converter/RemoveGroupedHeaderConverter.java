package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.Converter;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveGroupedHeaderStructure;

import java.util.Arrays;

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

        int defaultStartRow = rows[rows.length - 1] + 1;
        int defaultStartColumn = columns[columns.length - 1] + 1;
        int startRow = structure.startRow() == null ? defaultStartRow : structure.startRow();
        int startColumn = structure.startColumn() == null ? defaultStartColumn : structure.startColumn();

        int endRow = matrix.length;
        int endColumn = matrix[0].length;

        validateInputs(startRow, startColumn, endRow, endColumn);

        //Add header row dynamically based on grouped headers
        String defaultHeaderName = "undefined";
        int headerSize = rows.length + columns.length + 1;
        String[] headerRow = new String[headerSize];
        Arrays.fill(headerRow, defaultHeaderName);

        String[][] rowValues = new String[rows.length][endRow];
        for (int rowI : rows) {
            rowValues[rowI] = matrix[rowI];
        }

        int resultRows = (endRow - startRow) * (endColumn - startColumn);
        String[][] transformedMatrix = new String[resultRows + 1][]; // +1 wegen dem header
        int index = 0;

        //Header setzen
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

    private void validateInputs(int startRow, int startColumn, int endRow, int endColumn) {
        for (int rowIndex : structure.rows()) {
            if (rowIndex >= startRow) {
                throwCE("Row index must be less than startRow: " + startRow);
            }
            if (rowIndex >= endRow) {
                throwCE("Row index must be less than endRow: " + endRow);
            }
        }
        for (int columnIndex : structure.columns()) {
            if (columnIndex >= startColumn) {
                throwCE("Column index must be less than startColumn: " + startRow);
            }
            if (columnIndex >= endColumn) {
                throwCE("Column index must be less than endColumn: " + endRow);
            }
        }

        if (startRow >= endRow) {
            throwCE("Start row out of bounds");
        }
        if (startColumn >= endColumn) {
            throwCE("Start row out of bounds");
        }
    }

}
