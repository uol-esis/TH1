package de.uol.pgdoener.th1.business.infrastructure.csv_converter;

import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InputFile {

    private final MultipartFile file;
    private final TableStructureDto tableStructure;
    private final FileType fileType;

    private InputFile(MultipartFile file, TableStructureDto tableStructure, FileType fileType) {
        this.file = file;
        this.tableStructure = tableStructure;
        this.fileType = fileType;
    }

    /**
     * Constructor for InputFile
     *
     * @param file           the file to be read
     * @param tableStructure the table structure
     * @throws IllegalArgumentException if the file type is not supported
     */
    public InputFile(MultipartFile file, TableStructureDto tableStructure) {
        this(file, tableStructure, FileType.getType(file));
    }

    public String[][] asStringArray() throws IOException {
        return switch (fileType) {
            case CSV -> readCsvToMatrix();
            case EXCEL -> readExcelToMatrix();
        };
    }

    private String[][] readCsvToMatrix() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            final List<String[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line.split(String.valueOf(tableStructure.getDelimiter()), -1));
            }
            final int endRow = tableStructure.getEndRow();
            final int endCol = tableStructure.getEndColumn();
            final String[][] matrix = createMatrix();
            for (int i = 0; i < endRow; i++) {
                final String[] row = rows.get(i);
                System.arraycopy(row, 0, matrix[i], 0, endCol);
            }
            return matrix;
        }
    }

    private String[][] readExcelToMatrix() throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        //TODO handle multiple sheets
        Sheet sheet = sheetIterator.next();
        String[][] matrix = createMatrix();
        for (Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum >= tableStructure.getEndRow()) {
                break;
            }
            for (int i = 0; i < tableStructure.getEndColumn(); i++) {
                matrix[rowNum][i] = row.getCell(i).getStringCellValue();
            }
        }
        return matrix;
    }

    private String[][] createMatrix() {
        return new String[tableStructure.getEndRow()][tableStructure.getEndColumn()];
    }

}
