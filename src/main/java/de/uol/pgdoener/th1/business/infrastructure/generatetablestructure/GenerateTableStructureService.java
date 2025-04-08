package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class GenerateTableStructureService {
    private final InputFile inputFile;

    public GenerateTableStructureService(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    public TableStructureDto generateTableStructure() throws IOException {
        String[][] matrix = inputFile.asStringArray();
        MatrixInfo matrixInfo = getMatrixInfo(matrix);

        return getTableStructureFromMatrixInfo(matrixInfo);
    }

    private MatrixInfo getMatrixInfo(String[][] matrix) {
        MatrixInfo matrixInfo = new MatrixInfo();
        boolean dataStart = false;

        for (int i = 0; i < matrix.length; i++) {
            String[] row = matrix[i];
            RowInfo rowInfo = new RowInfo(i);

            for (int j = 0; j < row.length; j++) {
                if (isNumber(row[j])) {
                    dataStart = true;
                    break;
                }
                ColumnInfo colInfo = new ColumnInfo(j, !row[j].trim().isEmpty());
                rowInfo.addColumnInfo(colInfo);
                rowInfo.setHeaderName(row[0].trim());
            }

            if (dataStart) {
                break;
            }
            matrixInfo.addRowInfo(rowInfo);
        }

        return matrixInfo;
    }

    private TableStructureDto getTableStructureFromMatrixInfo(MatrixInfo matrixInfo) {
        TableStructureDto tableStructure = new TableStructureDto();
        tableStructure.setName(inputFile.getFileName());

        FillEmptyRowStructureDto fillEmptyRowStructure = new FillEmptyRowStructureDto();
        List<Integer> rowToFill = matrixInfo.getRowToFill();
        if (!rowToFill.isEmpty()) {
            fillEmptyRowStructure.setConverterType(ConverterTypeDto.FILL_EMPTY_ROW);
            fillEmptyRowStructure.setRowIndex(rowToFill);

            tableStructure.addStructuresItem(fillEmptyRowStructure);
        }

        // if(remove size and gesamt)
         /* List<Integer> rowIndexes = new ArrayList<>();
            List<Integer> colIndexes = new ArrayList<>();

            searchMatrix(matrix, rowIndexes, colIndexes);

            StructureDto removeRowByIndex = new StructureDto();
            StructureDto removeColByIndex = new StructureDto();
            if (!rowIndexes.isEmpty()) {
                removeRowByIndex.setConverterType(ConverterTypeDto.REMOVE_ROW_BY_INDEX);
                removeRowByIndex.setRowIndex(rowIndexes);

                tableStructure.addStructuresItem(removeRowByIndex);
            }
            if (!colIndexes.isEmpty()) {
                removeColByIndex.setConverterType(ConverterTypeDto.REMOVE_COLUMN_BY_INDEX);
                removeColByIndex.setColumnIndex(colIndexes);

                tableStructure.addStructuresItem(removeColByIndex);
            }*/

        if (matrixInfo.isGroupedHeader()) {
            RemoveGroupedHeaderStructureDto groupHeaderStructure = new RemoveGroupedHeaderStructureDto();
            groupHeaderStructure.setConverterType(ConverterTypeDto.REMOVE_GROUPED_HEADER);
            groupHeaderStructure.setColumnIndex(matrixInfo.getColumnIndexes());
            groupHeaderStructure.setRowIndex(matrixInfo.getRowIndexes());
            groupHeaderStructure.setStartRow(Optional.of(matrixInfo.getStartRow()));

            AddHeaderNameStructureDto addHeaderNamesStructure = new AddHeaderNameStructureDto();
            addHeaderNamesStructure.setConverterType(ConverterTypeDto.ADD_HEADER_NAME);
            addHeaderNamesStructure.setHeaderNames(matrixInfo.getHeaderNames());

            tableStructure.addStructuresItem(groupHeaderStructure);
            tableStructure.addStructuresItem(addHeaderNamesStructure);
        }
        return tableStructure;
    }

    // Methode zur Überprüfung, ob ein String eine Zahl ist (ganz oder mit Dezimalpunkt/Kommata)
    private boolean isNumber(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        str = str.trim().replace(",", "."); // Falls Komma als Dezimaltrenner genutzt wird
        return str.matches("-?\\d+(\\.\\d+)?"); // Regex für Ganzzahlen & Dezimalzahlen
    }

    public void searchMatrix(String[][] matrix, List<Integer> rowIndexes, List<Integer> colIndexes) {
        List<String> keywords = List.of(
                "sum", "total", "amount", "gesamt", "gesamtbetrag", "gesamtmenge", "summen", "totaal",
                "summe", "gesamtwert", "grand total", "aggregate", "grand total", "quantity"
        );

        // Durchsuche die erste Zeile
        for (int col = 0; col < matrix[0].length; col++) {
            String value = matrix[0][col].toLowerCase();
            for (String keyword : keywords) {
                if (value.contains(keyword)) {
                    colIndexes.add(col);  // Speichert den Index der Spalte, wenn ein Treffer in der ersten Zeile gefunden wurde
                }
            }
        }

        // Durchsuche die erste Spalte
        for (int row = 0; row < matrix.length; row++) {
            String value = matrix[row][0].toLowerCase();
            for (String keyword : keywords) {
                if (value.contains(keyword)) {
                    rowIndexes.add(row);  // Speichert den Index der Zeile, wenn ein Treffer in der ersten Spalte gefunden wurde
                }
            }
        }
    }

}
