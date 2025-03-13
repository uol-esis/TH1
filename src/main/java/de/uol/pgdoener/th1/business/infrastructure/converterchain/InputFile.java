package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
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
    private final TableStructureDto tableStructure;
    private final FileType fileType;

    private InputFile(MultipartFile file, TableStructureDto tableStructure, FileType fileType) {
        this.file = file;
        this.tableStructure = tableStructure;
        this.fileType = fileType;
        log.debug("Created InputFile with type: {}", fileType);
    }

    /**
     * Constructor for InputFile
     *
     * @param file           the file to be read
     * @param tableStructure the table structure
     * @throws IllegalArgumentException if the file type is not supported
     */
    public InputFile(@NonNull MultipartFile file, @NonNull TableStructureDto tableStructure) {
        this(file, tableStructure, FileType.getType(file));
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

    private String[][] readCsvToMatrix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = CSVFormat.EXCEL.builder()
                    .setDelimiter(tableStructure.getDelimiter())
                    .get()
                    .parse(reader)
                    .stream()
                    .map(r -> r.stream().toArray(String[]::new))
                    .toList();

            int maxRow = rows.size();
            if (maxRow == 0)
                return new String[0][0];
            int maxCol = rows.getFirst().length;

            // Falls endRow oder endColumn nicht gesetzt sind, bestimmen wir die Größe dynamisch
            int rowLength = tableStructure.getEndRow().orElse(maxRow);
            int colLength = tableStructure.getEndColumn().orElse(maxCol);
            // Matrix initialisieren
            String[][] matrix = new String[rowLength][colLength];

            // Daten in die Matrix kopieren
            for (int i = 0; i < rowLength && i < rows.size(); i++) {
                System.arraycopy(rows.get(i), 0, matrix[i], 0, Math.min(rows.get(i).length, colLength));
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
            int lastRow = tableStructure.getEndRow().orElse(sheet.getLastRowNum());
            int lastColumn = tableStructure.getEndColumn().orElse((int) sheet.getRow(0).getLastCellNum());
            String[][] matrix = new String[lastRow + 1][lastColumn];
            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum > lastRow) {
                    break;
                }
                for (int i = 0; i < lastColumn; i++) {
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
