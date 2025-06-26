package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder;

import de.uol.pgdoener.th1.business.dto.SplitRowReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ValueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindSplitRowService {

    /**
     * Analyzes the provided matrix to detect columns containing cells with multiple logical values.
     *
     * @param matrixInfo Metadata describing rows and columns
     * @param matrix     Actual table data in 2D String array
     * @return Optional list of columns that contain cells with multiple values (e.g. separated by newline or comma)
     */
    public Optional<List<SplitRowReportDto>> find(MatrixInfo matrixInfo, String[][] matrix) {
        List<SplitRowReportDto> rows = new ArrayList<>();
        for (ColumnInfo column : matrixInfo.columnInfos()) {
            String delimiter = findDelimiter(column, matrix);

            if (delimiter == null) {
                continue;
            }

            SplitRowReportDto report = new SplitRowReportDto();
            report.columnIndex(column.columnIndex());
            report.delimiter(delimiter);
            log.debug("Detected splitRowReport with '{}' in column {}", delimiter, column.columnIndex());
            rows.add(report);
        }

        return rows.isEmpty() ? Optional.empty() : Optional.of(rows);
    }

    // ========== Private helper methods ========== //

    /**
     * Finds a possible delimiter in a column indicating that at least one cell contains multiple values.
     *
     * @param column Column information (index and cells)
     * @param matrix Table content
     * @return The detected delimiter string or null if none found
     */
    private String findDelimiter(ColumnInfo column, String[][] matrix) {
        int maxChecks = 10;
        int checks = 0;

        String delimiter = null;
        for (CellInfo cell : column.cellInfos()) {
            if (cell.rowIndex() == 0) continue;
            if (cell.valueType() == ValueType.NUMBER) break;
            if (checks++ >= maxChecks) break;

            String cellContent = matrix[cell.rowIndex()][column.columnIndex()];
            delimiter = detectDelimiter(cellContent);
            if (delimiter == null) continue;
            break;
        }
        return delimiter;
    }


    /**
     * Tries to detect the most likely delimiter that separates multiple entries in a string.
     *
     * @param content The cell content to evaluate
     * @return The delimiter string (regex format) that best separates the content, or null if none found
     */
    private static String detectDelimiter(String content) {
        int maxItems = 1;
        String bestDelimiter = null;

        if (content == null || content.isBlank()) return bestDelimiter;

        for (String delimiter : POSSIBLE_DELIMITERS) {
            String[] parts = content.split(delimiter);
            int itemCount = countNonEmptyItems(parts);

            if (itemCount > maxItems) {
                maxItems = itemCount;
                bestDelimiter = delimiter;
            }
        }

        return bestDelimiter;
    }

    /**
     * Counts how many non-empty trimmed entries exist in a string array.
     *
     * @param parts The string array to inspect
     * @return The number of non-blank, trimmed strings
     */
    private static int countNonEmptyItems(String[] parts) {
        int count = 0;
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * List of possible delimiters to check against cell content.
     */
    private static final String[] POSSIBLE_DELIMITERS = {
            "\\r?\\n",   // Line break (Unix or Windows)
    };

}
