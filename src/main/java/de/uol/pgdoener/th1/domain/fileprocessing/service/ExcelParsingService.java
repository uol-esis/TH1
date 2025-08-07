package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.fileprocessing.WorkbookFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParsingService {

    private final DateNormalizerService dateNormalizerService;
    private final NumberNormalizer numberNormalizer;

    public String[][] parseExcel(InputStream inputStream, WorkbookFactory workbookFactory) throws IOException {
        try (Workbook workbook = workbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

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

    private String getValueForCell(Cell cell) {
        if (cell == null) return "";
        return getValueForType(cell, cell.getCellType());
    }

    private String getValueForType(Cell cell, CellType cellType) {
        try {
            return switch (cellType) {
                case STRING -> {
                    String value = cell.getStringCellValue();

                    String maybeDate = dateNormalizerService.tryNormalize(value);
                    if (maybeDate != null) yield maybeDate;

                    String maybeNumber = numberNormalizer.normalizeFormat(value);
                    if (maybeNumber != null) yield maybeNumber;

                    yield value;
                }
                case NUMERIC -> numberNormalizer.formatNumeric(cell.getNumericCellValue());
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

