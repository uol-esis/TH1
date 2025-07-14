package de.uol.pgdoener.th1.application.infrastructure;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.uol.pgdoener.th1.application.infrastructure.exceptions.InputFileException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.StreamSupport;

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
            String[][] contentWithNulls = switch (fileType) {
                case CSV -> readCsvToMatrix();
                case EXCEL_OLE2 -> readExcelOLE2ToMatrix();
                case EXCEL_OOXML -> readExcelOOXMLToMatrix();
            };
            String[][] content = mapNulls(contentWithNulls);
            return ensureSameLengths(content);
        } catch (IOException e) {
            throw new InputFileException("Could not read file", e);
        }
    }

    public String getFileName() {
        String originalFilename = Objects.requireNonNull(this.file.getOriginalFilename());
        int dotIndex = originalFilename.lastIndexOf('.');
        String filenameWithoutExtension = (dotIndex == -1) ? originalFilename : originalFilename.substring(0, dotIndex);

        // Alle Zahlen entfernen (inkl. mögliche Datumsmuster)
        String cleaned = filenameWithoutExtension.replaceAll("\\d+", "");

        // Optional: Leerzeichen trimmen und evtl. doppelte Unterstriche oder Bindestriche bereinigen
        cleaned = cleaned.replaceAll("[_\\-]{2,}", "_").replaceAll("^[_\\-]+|[_\\-]+$", "").trim();
        return cleaned;

    }

    // #################
    // Internal Methods
    // #################

    // https://stackoverflow.com/questions/49235863/how-to-determine-the-delimiter-in-csv-file
    private String getDelimiter() throws IOException {
        CsvParserSettings settings = new CsvParserSettings();
        settings.detectFormatAutomatically();

        CsvParser parser = new CsvParser(settings);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            parser.parseAll(reader);
        }
        return parser.getDetectedFormat().getDelimiterString();
    }

    private String[][] readCsvToMatrix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String delimiter = getDelimiter();
            log.debug("Detected delimiter: {}", delimiter);

            CSVFormat format = CSVFormat.DEFAULT
                    .withDelimiter(delimiter.charAt(0))
                    .withQuote('"')
                    .withIgnoreEmptyLines(true)
                    .withTrim();

            List<String[]> rows = new ArrayList<>();

            for (CSVRecord record : format.parse(reader)) {
                String[] row = StreamSupport.stream(record.spliterator(), false)
                        .toArray(String[]::new);
                rows.add(row);
            }

            if (rows.isEmpty()) {
                return new String[0][0];
            }

            return rows.toArray(new String[rows.size()][]);
        }
    }

    private String[][] readExcelOLE2ToMatrix() throws IOException {
        return readExcelToMatrix(HSSFWorkbook::new);
    }

    private String[][] readExcelOOXMLToMatrix() throws IOException {
        return readExcelToMatrix(XSSFWorkbook::new);
    }

    private String[][] readExcelToMatrix(WorkbookFactory workbookFactory) throws IOException {
        try (Workbook workbook = workbookFactory.create(file.getInputStream())) {
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            //TODO handle multiple sheets
            Sheet sheet = sheetIterator.next();
            int endRow = sheet.getLastRowNum() + 1;
            if (endRow == 0 || sheet.getRow(0) == null || sheet.getRow(0).getLastCellNum() == -1) {
                return new String[0][0];
            }
            int endColumn = sheet.getRow(0).getLastCellNum();
            String[][] matrix = new String[endRow][endColumn];
            for (Row row : sheet) {
                int rowNum = row.getRowNum();

                if (rowNum >= endRow) return matrix;

                for (int i = 0; i < endColumn; i++) {
                    if (row.getCell(i) == null) {
                        matrix[rowNum][i] = "";
                        continue;
                    }
                    switch (row.getCell(i).getCellType()) {
                        case STRING -> matrix[rowNum][i] = row.getCell(i).getStringCellValue();
                        case NUMERIC -> matrix[rowNum][i] = String.valueOf(row.getCell(i).getNumericCellValue());
                        case BOOLEAN -> matrix[rowNum][i] = String.valueOf(row.getCell(i).getBooleanCellValue());
                        case FORMULA -> matrix[rowNum][i] = row.getCell(i).getCellFormula();
                        case BLANK -> matrix[rowNum][i] = "";
                        case ERROR -> matrix[rowNum][i] = "ERROR";
                        default -> matrix[rowNum][i] = "UNKNOWN";
                    }
                }
            }
            return matrix;
        }
    }

    private interface WorkbookFactory {
        Workbook create(InputStream inputStream) throws IOException;
    }

    // ################
    // Post Processing
    // ################

    private String[][] mapNulls(String[][] raw) {
        for (int i = 0; i < raw.length; i++) {
            for (int j = 0; j < raw[i].length; j++) {
                if (raw[i][j] == null) {
                    raw[i][j] = "";
                }
            }
        }
        return raw;
    }

    private String[][] ensureSameLengths(String[][] raw) {
        int maxLength = Arrays.stream(raw)
                .mapToInt(row -> row.length)
                .max()
                .orElse(0);

        String[][] result = new String[raw.length][maxLength];
        for (int i = 0; i < raw.length; i++) {
            System.arraycopy(raw[i], 0, result[i], 0, raw[i].length);
            for (int j = raw[i].length; j < maxLength; j++) {
                result[i][j] = "";
            }
        }
        return result;
    }

}
