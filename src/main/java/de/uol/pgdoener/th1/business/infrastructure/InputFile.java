package de.uol.pgdoener.th1.business.infrastructure;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

/**
 * This class takes a {@link MultipartFile} and a {@link TableStructureDto} and provides the file as a 2D-String Array.
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
     * @throws IllegalArgumentException if the file type is not supported
     */
    public InputFile(@NonNull MultipartFile file) {
        this(file, FileType.getType(file));
    }

    /**
     * Returns the content of the file to a 2D-String Array.
     * Each entry is a cell in the CSV or Excel File.
     *
     * @return the contents of the file
     * @throws IOException if the file cannot be read
     */
    public String[][] asStringArray() throws IOException {
        return mapNulls(cutOff(switch (fileType) {
            case CSV -> readCsvToMatrix();
            case EXCEL_OLE2 -> readExcelOLE2ToMatrix();
            case EXCEL_OOXML -> readExcelOOXMLToMatrix();
        }));
    }

    public String getFileName() {
        String originalFilename = Objects.requireNonNull(this.file.getOriginalFilename()).toLowerCase();
        int dotIndex = originalFilename.lastIndexOf('.');
        String nameWithoutExtension = (dotIndex == -1) ? originalFilename : originalFilename.substring(0, dotIndex);
        return nameWithoutExtension.replace(" ", "_");
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
            List<String[]> rows = reader.lines()
                    .map(String::trim)
                    .map(line -> line.split(delimiter, -1))
                    .toList();

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
    // Cut Off Methods
    // ################

    private String[][] cutOff(String[][] raw) {
        // cut off rows before the first non-empty row
        int firstRelevantRow = -1;
        for (int i = 0; i < raw.length; i++) {
            if (isFilterRow(raw[i])) {
                firstRelevantRow = i;
            } else {
                break;
            }
        }
        List<String[]> rows = new ArrayList<>(Arrays.asList(raw));
        if (firstRelevantRow != -1) {
            rows = new ArrayList<>(Arrays.asList(raw).subList(firstRelevantRow + 1, raw.length));
            log.debug("Cut off {} rows from the beginning", firstRelevantRow + 1);
        }
        final int rowCountAfterCutOff = rows.size();

        // cut off rows after the last non-empty row
        Iterator<String[]> rowIterator = rows.reversed().iterator();
        while (rowIterator.hasNext()) {
            String[] row = rowIterator.next();
            if (isFilterRow(row)) {
                rowIterator.remove();
            } else {
                break;
            }
        }
        log.debug("Cut off {} rows from the end", rowCountAfterCutOff - rows.size());

        return rows.toArray(new String[rows.size()][]);
    }

    private boolean isFilterRow(String[] row) {
        // filter out empty rows
        if (row.length == 0)
            return true;

        int blankCells = 0;
        int nonBlankCells = 0;
        for (String cell : row) {
            if (cell == null || cell.isBlank()) {
                blankCells++;
            } else {
                nonBlankCells++;
            }
        }

        // filter out rows with only blank cells
        if (blankCells == row.length)
            return true;

        // filter out rows with only one non-blank cell
        return nonBlankCells == 1;
    }

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

}
