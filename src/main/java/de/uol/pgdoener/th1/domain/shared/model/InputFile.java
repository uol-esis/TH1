package de.uol.pgdoener.th1.domain.shared.model;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.uol.pgdoener.th1.domain.shared.exceptions.InputFileException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class takes a {@link MultipartFile} and provides the file as a 2D-String Array.
 */
@Slf4j
public class InputFile {

    private final MultipartFile file;
    private final FileType fileType;

    private InputFile(MultipartFile file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
        log.debug("Created InputFile with type: {}", fileType);
    }

    /**
     * Constructor for InputFile
     *
     * @param file the file to be read
     * @throws InputFileException if the file type is not supported
     */
    public InputFile(@NonNull MultipartFile file) throws InputFileException {
        this(file, FileType.getType(file));
    }

    /**
     * Returns the content of the file to a 2D-String Array.
     * Each entry is a cell in the CSV or Excel File.
     * Entries cannot be null, but can be empty.
     * All rows have the same length.
     *
     * @return the contents of the file
     * @throws InputFileException if the file cannot be read
     */
    public String[][] asStringArray() throws InputFileException {
        try {
            String[][] matrix = switch (fileType) {
                case CSV -> csvToMatrix(file.getInputStream());
                case EXCEL_OLE2 -> excelToMatrix(file.getInputStream(), HSSFWorkbook::new);
                case EXCEL_OOXML -> excelToMatrix(file.getInputStream(), XSSFWorkbook::new);
            };
            System.out.println(Arrays.deepToString(matrix));
            return matrix;
        } catch (IOException e) {
            throw new InputFileException("Could not read file", e);
        }
    }

    public String getFileName() {
        String originalFilename = Objects.requireNonNull(this.file.getOriginalFilename());
        int dotIndex = originalFilename.lastIndexOf('.');
        String filenameWithoutExtension = (dotIndex == -1) ? originalFilename : originalFilename.substring(0, dotIndex);

        String cleaned = filenameWithoutExtension.replaceAll("\\d+", "");

        cleaned = cleaned.replaceAll("[_\\-]{2,}", "_").replaceAll("^[_\\-]+|[_\\-]+$", "").trim();
        return cleaned;
    }

    // -----------------------------
    // Private Helper Methods Below
    // -----------------------------

    private String[][] csvToMatrix(InputStream inputStream) throws IOException {
        char delimiter = detectDelimiter(inputStream);
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter)
                .setQuote('"')
                .setIgnoreEmptyLines(true)
                .setTrim(true).get();
        try (
                Reader reader = new InputStreamReader(inputStream);
                CSVParser parser = format.parse(reader)
        ) {
            List<String[]> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                int size = record.size();
                String[] row = new String[size];
                for (int i = 0; i < size; i++) {
                    row[i] = record.get(i);
                }
                rows.add(row);
            }
            return rows.toArray(new String[0][0]);
        }
    }

    private String[][] excelToMatrix(InputStream inputStream, WorkbookFactory workbookFactory) throws IOException {
        Workbook workbook = workbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        int rowHeight = sheet.getLastRowNum() + 1;
        int columnWidth = getColumnWidth(sheet);

        String[][] matrix = new String[rowHeight][columnWidth];

        for (int i = 0; i < rowHeight; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                Arrays.fill(matrix[i] = new String[columnWidth], "");
                continue;
            }
            for (int j = 0; j < columnWidth; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    matrix[i][j] = "";
                    continue;
                }
                matrix[i][j] = getValueForType(cell, evaluator);
            }
        }

        System.out.println(Arrays.deepToString(matrix));

        return matrix;
    }

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

    private String getValueForType(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";

        CellType cellType = cell.getCellType();

        return switch (cellType) {
            case STRING -> {
                String value = cell.getStringCellValue();
                String maybeDate = tryParseDate(value);
                yield maybeDate != null ? maybeDate : value;
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield formatDate(cell.getDateCellValue());
                }
                yield formatNumeric(cell.getNumericCellValue());
            }
            case FORMULA -> getValueForEvaluated(cell, evaluator);
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> "";
            case ERROR -> "ERROR";
            default -> "UNKNOWN";
        };
    }

    private String tryParseDate(String value) {
        String[] patterns = {
                "dd.MM.yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "MM/dd/yyyy"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate date = LocalDate.parse(value, formatter);
                return date.toString(); // oder formatDate(Date) falls du Konsistenz willst
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private String getValueForEvaluated(Cell cell, FormulaEvaluator evaluator) {
        CellValue evaluated = evaluator.evaluate(cell);
        if (evaluated == null) return "";
        return switch (evaluated.getCellType()) {
            case BOOLEAN -> String.valueOf(evaluated.getBooleanValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield formatDate(cell.getDateCellValue());
                }
                yield formatNumeric(evaluated.getNumberValue());
            }
            case STRING -> evaluated.getStringValue();
            case BLANK -> "";
            case ERROR -> "ERROR";
            default -> "UNKNOWN";
        };
    }

    private String formatDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private String formatNumeric(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return String.format("%.10f", number).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
    }

    private char detectDelimiter(InputStream input) throws IOException {
        input.mark(10 * 1024 * 1024);

        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically();

        CsvParser parser = new CsvParser(settings);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            parser.parseAll(reader);
        }

        input.reset();

        return parser.getDetectedFormat().getDelimiter();
    }

    private interface WorkbookFactory {
        Workbook create(InputStream inputStream) throws IOException;
    }
}
