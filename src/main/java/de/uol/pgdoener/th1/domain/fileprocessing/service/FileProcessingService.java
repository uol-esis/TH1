package de.uol.pgdoener.th1.domain.fileprocessing.service;

import de.uol.pgdoener.th1.domain.fileprocessing.helper.DetectDelimiterService;
import de.uol.pgdoener.th1.domain.shared.exceptions.InputFileException;
import de.uol.pgdoener.th1.domain.shared.model.FileType;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final CsvParsingService csvParsingService;
    private final ExcelOOXMLParsingService excelOOXMLParsingService;
    private final ExcelOLE2ParsingService excelOLE2ParsingService;
    private final DetectDelimiterService detectDelimiterService;

    /**
     * Service responsible for processing uploaded files (CSV and Excel formats).
     * <p>
     * - Detects the file type (CSV, Excel OLE2, Excel OOXML) using {@link FileType} <br>
     * - Delegates parsing to the corresponding service:
     *   <ul>
     *     <li>{@link CsvParsingService} for CSV files (delimiter automatically detected via {@link DetectDelimiterService})</li>
     *     <li>{@link ExcelOLE2ParsingService} for old Excel files (HSSF / .xls)</li>
     *     <li>{@link ExcelOOXMLParsingService} for modern Excel files (XSSF / .xlsx)</li>
     *   </ul>
     * - Returns the parsed data as a 2D String array.
     */
    public String[][] process(MultipartFile file, Optional<Integer> page) throws IOException, InputFileException {
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
            case EXCEL_OOXML -> {
                try (InputStream stream = file.getInputStream()) {
                    return excelOOXMLParsingService.readExcel(stream, page);
                }
            }
            case EXCEL_OLE2 -> {
                try (InputStream stream = file.getInputStream()) {
                    return excelOLE2ParsingService.readExcel(stream, XSSFWorkbook::new, page);
                }
            }
            default -> throw new InputFileException("Unsupported file type");
        }
    }
}

