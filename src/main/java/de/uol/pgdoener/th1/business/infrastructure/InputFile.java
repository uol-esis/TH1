package de.uol.pgdoener.th1.business.infrastructure;

import de.uol.pgdoener.th1.business.dto.TableStructureDto;
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
import java.util.Iterator;
import java.util.List;

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
    public InputFile(MultipartFile file) {
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
        return switch (fileType) {
            case CSV -> readCsvToMatrix();
            case EXCEL_OLE2 -> readExcelOLE2ToMatrix();
            case EXCEL_OOXML -> readExcelOOXMLToMatrix();
        };
    }

    // #################
    // Internal Methods
    // #################

    private String getDelimiter() {
        return ";";
    }

    private String[][] readCsvToMatrix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String delimiter = getDelimiter();
            List<String[]> rows = reader.lines()
                    .map(String::trim)
                    .filter(line -> line.contains(delimiter) && !line.startsWith("\""))
                    .map(line -> line.split(delimiter, -1))
                    .filter(row -> row.length > 0 && !row[0].isEmpty())
                    .toList();

            if (rows.isEmpty()) {
                return new String[0][0];
            }

            int maxCol = rows.getFirst().length;
            String[][] matrix = new String[rows.size()][maxCol];

            for (int i = 0; i < rows.size(); i++) {
                System.arraycopy(rows.get(i), 0, matrix[i], 0, rows.get(i).length);
            }

            return matrix;
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
            int endRow = sheet.getLastRowNum();
            int endColumn = sheet.getRow(0).getLastCellNum();
            String[][] matrix = new String[endRow][endColumn];
            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum >= endRow) {
                    break;
                }
                for (int i = 0; i < endColumn; i++) {
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
}
