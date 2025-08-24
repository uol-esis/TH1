package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.fileprocessing.WorkbookFactory;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.DateNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.NumberNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.TypeDetector;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.ValueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParsingService {

    private final DateNormalizerService dateNormalizerService;
    private final NumberNormalizerService numberNormalizerService;
    private final TypeDetector typeDetector;

    /**
     * Parses the first sheet of an Excel file into a 2D String array.
     * <p>
     * Each cell is normalized:
     * <ul>
     *   <li>Dates are converted to a standardized format (via {@link DateNormalizerService})</li>
     *   <li>Numbers are formatted consistently (via {@link NumberNormalizerService})</li>
     *   <li>Empty cells become empty strings</li>
     * </ul>
     *
     * @param inputStream     the InputStream of the Excel file
     * @param workbookFactory a factory for creating {@link Workbook} instances
     * @return a 2D String array containing the parsed and normalized sheet data
     * @throws IOException if the file cannot be read or parsed
     */
    public String[][] parseExcel(InputStream inputStream, WorkbookFactory workbookFactory, Optional<Integer> page) throws IOException {
        try (Workbook workbook = workbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(page.orElse(0));

            int rowCount = sheet.getLastRowNum() + 1;
            int colCount = getColumnWidth(sheet);

            String[][] matrix = new String[rowCount][colCount];

            for (int i = 0; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    Arrays.fill(matrix[i], "");
                    continue;
                }
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    matrix[i][j] = getValueForCell(cell);
                }
            }
            return matrix;
        }
    }

    // ----------------- Private helper methods ----------------- //

    /**
     * Determines the maximum number of columns in the sheet by checking each row.
     * Stops scanning if a shorter row is found after a longer one.
     *
     * @param sheet the Excel sheet
     * @return the maximum column count
     */
    private int getColumnWidth(Sheet sheet) {
        int maxColumnWidth = 0;
        for (Row row : sheet) {
            int columnLength = sheet.getRow(row.getRowNum()).getLastCellNum();

            if (columnLength > maxColumnWidth) {
                maxColumnWidth = columnLength;
            } else {
                break;
            }
        }
        return maxColumnWidth;
    }

    /**
     * Retrieves a normalized value for a given cell.
     * If the cell is null, returns an empty string.
     *
     * @param cell the cell to read
     * @return the normalized value as a String
     */
    private String getValueForCell(Cell cell) {
        if (cell == null) return "";
        return getValueForType(cell, cell.getCellType());
    }

    /**
     * Converts a cell value into a normalized String based on its {@link CellType}.
     * Handles special cases for formulas, dates, and numbers.
     *
     * @param cell     the cell to read
     * @param cellType the type of the cell
     * @return the normalized value as a String
     */
    private String getValueForType(Cell cell, CellType cellType) {
        try {
            return switch (cellType) {
                case STRING -> {
                    String value = cell.getStringCellValue();
                    ValueType valueType = typeDetector.detect(value);

                    yield switch (valueType) {
                        case NUMBER:
                            yield numberNormalizerService.normalizeFormat(value);
                        case DATE:
                            yield dateNormalizerService.tryNormalize(value);
                        case TEXT, TIMESTAMP, BOOLEAN, UUID:
                            yield value;
                    };
                }
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield dateNormalizerService.tryNormalize(cell.getDateCellValue());
                    }
                    yield numberNormalizerService.formatNumeric(cell.getNumericCellValue());
                }
                case FORMULA -> {
                    CellType cached = cell.getCachedFormulaResultType();
                    yield cached != null ? getValueForType(cell, cached) : "UNRESOLVED FORMULA";
                }
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case BLANK -> "";
                case ERROR -> "ERROR";
                default -> "UNKNOWN TYPE";
            };
        } catch (Exception e) {
            log.warn("Error reading cell at row={}, col={}: {}", cell.getRowIndex(), cell.getColumnIndex(), e.getMessage());
            return "UNKNOWN RESULT";
        }

    }
}

