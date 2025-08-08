package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.shared.exceptions.InputFileException;
import de.uol.pgdoener.th1.domain.shared.model.FileType;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final CsvParsingService csvParsingService;
    private final ExcelParsingService excelParsingService;
    private final DetectDelimiterService detectDelimiterService;

    /**
     * Service responsible for processing uploaded files (CSV and Excel formats).
     * <p>
     * - Detects the file type (CSV, Excel OLE2, Excel OOXML) using {@link FileType} <br>
     * - Delegates parsing to the corresponding service:
     *   <ul>
     *     <li>{@link CsvParsingService} for CSV files (delimiter automatically detected via {@link DetectDelimiterService})</li>
     *     <li>{@link ExcelParsingService} for Excel files (HSSF or XSSF)</li>
     *   </ul>
     * - Returns the parsed data as a 2D String array.
     */
    public String[][] process(MultipartFile file) throws IOException, InputFileException {
        FileType fileType = FileType.getType(file);

        switch (fileType) {
            case CSV -> {
                String delimiter;
                try (InputStream is1 = file.getInputStream()) {
                    delimiter = detectDelimiterService.detect(is1);
                }

                try (InputStream is2 = file.getInputStream()) {
                    return csvParsingService.parseCsv(is2, delimiter);
                }
            }
            case EXCEL_OLE2 -> {
                try (InputStream stream = file.getInputStream()) {
                    return excelParsingService.parseExcel(stream, HSSFWorkbook::new);
                }
            }
            case EXCEL_OOXML -> {
                try (InputStream stream = file.getInputStream()) {
                    return excelParsingService.parseExcel(stream, XSSFWorkbook::new);
                }
            }
            default -> throw new InputFileException("Unsupported file type");
        }
    }
}

