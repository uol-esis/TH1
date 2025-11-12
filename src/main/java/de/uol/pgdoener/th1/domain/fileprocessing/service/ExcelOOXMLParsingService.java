package de.uol.pgdoener.th1.domain.fileprocessing.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.DateNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.NumberNormalizerService;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.TypeDetector;
import de.uol.pgdoener.th1.domain.fileprocessing.helper.ValueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelOOXMLParsingService {

    private final DateNormalizerService dateNormalizerService;
    private final NumberNormalizerService numberNormalizerService;
    private final TypeDetector typeDetector;

    private static final String EMPTY = "";

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
     * @param inputStream the InputStream of the Excel file
     * @return a 2D String array containing the parsed and normalized sheet data
     */
    public String[][] readExcel(InputStream inputStream, java.util.Optional<Integer> page) {
        List<List<String>> sheetData = new ArrayList<>();
        int sheetIndex = page.orElse(0);

        EasyExcel.read(inputStream, new ReadListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> row, AnalysisContext context) {
                List<String> rowData = new ArrayList<>();
                int maxIndex = row.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1);
                for (int i = 0; i <= maxIndex; i++) {
                    rowData.add(getValueForObject(row.getOrDefault(i, EMPTY)));
                }
                sheetData.add(rowData);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        }).sheet(sheetIndex).headRowNumber(0).doRead();

        return toStringArray(sheetData);
    }

    // ----------------- Private helper methods ----------------- //

    /**
     * Converts a cell value into a normalized String based on its CellType.
     * Handles special cases for formulas, dates, and numbers.
     *
     * @param cell the cell to read
     * @return the normalized value as a String
     */
    private String getValueForObject(Object cell) {
        if (cell == null) return "";
        try {
            return switch (cell) {
                case String str -> {
                    ValueType type = typeDetector.detect(str);
                    yield switch (type) {
                        case NUMBER -> numberNormalizerService.normalizeFormat(str);
                        case DATE -> dateNormalizerService.tryNormalize(str);
                        case TEXT, TIMESTAMP, BOOLEAN, UUID -> str;
                    };
                }
                case Number num -> numberNormalizerService.formatNumeric(num.doubleValue());
                case Date date -> dateNormalizerService.tryNormalize(date);
                case Boolean b -> String.valueOf(b);
                default -> cell.toString();
            };
        } catch (Exception e) {
            log.warn("Error normalizing value '{}': {}", cell, e.getMessage());
            return "UNKNOWN RESULT";
        }
    }

    private String[][] toStringArray(List<List<String>> sheetData) {
        int rows = sheetData.size();
        int cols = sheetData.stream().mapToInt(List::size).max().orElse(0);
        String[][] result = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            List<String> row = sheetData.get(i);
            for (int j = 0; j < row.size(); j++) {
                result[i][j] = row.get(j);
            }
        }
        return result;
    }
}




