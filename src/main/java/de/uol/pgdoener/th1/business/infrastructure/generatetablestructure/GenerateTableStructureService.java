package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

// Remove empty cells at the end in the row
// mehrer Einträge in einer Zeile -> Converter Schreiben der eine column index braucht und dann nach abstätzrn /n die eintrage entschachtelt.
// Wie gehen wir mit leeren Werten um ? // wichtig für die Datenbank kann mit * und anderen Symbolen nicht umgehen :(
// Bulk import fix Postgres
// Code schön machen

/**
 * Service for generating table structure from a given input file.
 */
@Slf4j
public class GenerateTableStructureService {
    private final InputFile inputFile;

    public GenerateTableStructureService(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Main entry point to generate a table structure from the input file.
     *
     * @return the generated table structure DTO.
     * @throws IOException if the file cannot be read.
     */
    public TableStructureDto generateTableStructure() throws IOException {
        try {
            log.info("Start generating table structure for file: {}", inputFile.getFileName());
            String[][] matrix = inputFile.asStringArray();
            MatrixInfo matrixInfo = extractMatrixInfo(matrix);
            TableStructureDto tableStructure = buildTableStructure(matrixInfo);
            log.info("Successfully generated table structure: {}", tableStructure.getName());
            return tableStructure;
        } catch (IOException e) {
            log.error("Failed to read input file: {}", inputFile.getFileName(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during table structure generation", e);
            throw new RuntimeException("Tabellenstruktur konnte nicht erstellt werden", e);
        }
    }

    /**
     * Extracts metadata from the top rows of the matrix until the data row begins.
     *
     * @param matrix the raw matrix read from the file
     * @return MatrixInfo object with structure metadata
     */
    private MatrixInfo extractMatrixInfo(String[][] matrix) {
        MatrixInfo matrixInfo = new MatrixInfo();

        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            String[] row = matrix[rowIndex];

            if (isDataRow(row)) {
                log.debug("Detected start of data at row: {}", rowIndex);
                break;
            }

            RowInfo rowInfo = extractRowInfo(row, rowIndex);
            matrixInfo.addRowInfo(rowInfo);
        }
        return matrixInfo;
    }

    /**
     * Determines the maximum column length needed by finding the rightmost valid element.
     * Valid = not null, not empty, not equal to "*"
     */
    private int getMaxColumnCount(String[][] inputMatrix) {
        int maxColumnCount = 0;
        ///  Muss nicht für alle sein
        for (String[] row : inputMatrix) {

            // Traverse from right to left to find the last valid entry
            for (int colIndex = row.length - 1; colIndex >= 0; colIndex--) {
                String entry = row[colIndex];

                if (isInvalidEntry(entry)) {
                    continue;
                }

                maxColumnCount = Math.max(maxColumnCount, colIndex + 1);
                break;
            }
        }
        return maxColumnCount;
    }

    /**
     * Determines if a given row is a data row (based on numeric cell values).
     */
    private boolean isDataRow(String[] row) {
        for (String cell : row) {
            if (isNumeric(cell)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a string row into a RowInfo object with CellInfos.
     */
    private RowInfo extractRowInfo(String[] row, int rowIndex) {
        RowInfo rowInfo = new RowInfo(rowIndex);

        for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
            String cell = row[cellIndex].trim();
            boolean isNotEmpty = !cell.isEmpty();
            rowInfo.addColumnInfo(new CellInfo(cellIndex, isNotEmpty));
        }

        rowInfo.setHeaderName(row[0].trim());
        return rowInfo;
    }

    /**
     * Constructs the full table structure with necessary converters applied.
     */
    private TableStructureDto buildTableStructure(MatrixInfo matrixInfo) {
        TableStructureDto tableStructure = new TableStructureDto();
        tableStructure.setName(inputFile.getFileName());

        StructureDto removeInvalidRowsStructure = buildRemoveRowsStructure;

        StructureDto removeHeaderStructure = buildRemoveHeaderStructure(matrixInfo);
        tableStructure.addStructuresItem(removeHeaderStructure);

        StructureDto removeFooterStructure = buildRemoveFooterStructure(matrixInfo);
        tableStructure.addStructuresItem(removeFooterStructure);

        if (matrixInfo.hasEmptyRow()) {
            StructureDto fillEmptyRowStructure = buildFillEmptyRowStructure(matrixInfo);

            tableStructure.addStructuresItem(fillEmptyRowStructure);
        }

        if (matrixInfo.hasGroupedHeader()) {
            StructureDto groupHeaderStructure = buildGroupHeaderStructure(matrixInfo);
            StructureDto addHeaderNameStructure = buildHeaderNameStructure(matrixInfo);

            tableStructure.addStructuresItem(groupHeaderStructure);
            tableStructure.addStructuresItem(addHeaderNameStructure);
        }
        return tableStructure;
    }

    /**
     * Builds converter structure for removing header rows.
     */
    private StructureDto buildRemoveRowsStructure(MatrixInfo matrixInfo) {
        log.debug("Start buildRemoveRowsStructure");
        RemoveInvalidRowsStructureDto removeInvalidRowStructure = new RemoveInvalidRowsStructureDto();
        removeInvalidRowStructure.setConverterType(ConverterTypeDto.REMOVE_INVALID_ROWS);
        log.debug("Finish buildRemoveRowsStructure");
        return removeInvalidRowStructure;
    }

    /**
     * Builds converter structure for removing header rows.
     */
    private StructureDto buildRemoveHeaderStructure(MatrixInfo matrixInfo) {
        log.debug("Start buildRemoveHeaderStructure");
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto();
        removeHeaderStructure.setConverterType(ConverterTypeDto.REMOVE_HEADER);
        log.debug("Finish buildRemoveHeaderStructure");
        return removeHeaderStructure;
    }

    /**
     * Builds converter structure for removing footer rows.
     */
    private StructureDto buildRemoveFooterStructure(MatrixInfo matrixInfo) {
        log.debug("Start buildRemoveFooterStructure");
        RemoveFooterStructureDto removeFooterStructure = new RemoveFooterStructureDto();
        removeFooterStructure.setConverterType(ConverterTypeDto.REMOVE_HEADER);
        log.debug("Finish buildRemoveFooterStructure");
        return removeFooterStructure;
    }

    /**
     * Builds converter structure for removing grouped header rows.
     */
    private StructureDto buildGroupHeaderStructure(MatrixInfo matrixInfo) {
        log.debug("Start buildGroupHeaderStructure");
        RemoveGroupedHeaderStructureDto groupHeaderStructure = new RemoveGroupedHeaderStructureDto();
        groupHeaderStructure.setConverterType(ConverterTypeDto.REMOVE_GROUPED_HEADER);
        groupHeaderStructure.setColumnIndex(matrixInfo.getColumnIndexes());
        groupHeaderStructure.setRowIndex(matrixInfo.getRowIndexes());
        groupHeaderStructure.setStartRow(Optional.of(matrixInfo.getStartRow()));
        log.debug("Finish buildGroupHeaderStructure");

        return groupHeaderStructure;
    }

    /**
     * Builds converter structure for setting header names.
     */
    private StructureDto buildHeaderNameStructure(MatrixInfo matrixInfo) {
        log.debug("Start buildHeaderNameStructure");
        AddHeaderNameStructureDto addHeaderNamesStructure = new AddHeaderNameStructureDto();
        addHeaderNamesStructure.setConverterType(ConverterTypeDto.ADD_HEADER_NAME);
        addHeaderNamesStructure.setHeaderNames(matrixInfo.getHeaderNames());
        log.debug("Finish buildHeaderNameStructure");

        return addHeaderNamesStructure;
    }

    /**
     * Builds converter structure to fill partially filled rows.
     */
    private StructureDto buildFillEmptyRowStructure(MatrixInfo matrixInfo) {
        log.debug("Start buildFillEmptyRowStructure");
        FillEmptyRowStructureDto fillEmptyRowStructure = new FillEmptyRowStructureDto();
        fillEmptyRowStructure.setConverterType(ConverterTypeDto.FILL_EMPTY_ROW);
        fillEmptyRowStructure.setRowIndex(matrixInfo.getRowToFill());
        log.debug("Finish buildFillEmptyRowStructure");

        return fillEmptyRowStructure;
    }

    /**
     * Determines if a string is numeric (supports decimals with dot or comma).
     */
    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        str = str.trim().replace(",", "."); // Falls Komma als Dezimaltrenner genutzt wird
        return str.matches("-?\\d+(\\.\\d+)?"); // Regex für Ganzzahlen & Dezimalzahlen
    }


    /**
     * Counts the number of valid elements in a row.
     * Valid = not null, not empty, not equal to "*"
     */
    private long countValidElements(String[] row) {
        return Arrays.stream(row)
                .filter(entry -> !isInvalidEntry(entry))
                .count();
    }

    /**
     * Returns true if the entry is considered invalid.
     * Invalid = null, empty string, or a literal "*"
     */
    private boolean isInvalidEntry(String entry) {
        return entry == null || entry.trim().isEmpty() || entry.equals("*");
    }
}
