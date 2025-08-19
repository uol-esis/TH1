package de.uol.pgdoener.th1.domain.infrastructure;

import de.uol.pgdoener.th1.domain.fileprocessing.service.FileProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class FileProcessingServiceTest {

    @Autowired
    private FileProcessingService fileProcessingService;

    @Test
    public void testCsvWithVariousNumberAndDateFormats() throws Exception {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("test/mixed_formats.csv");

        assert csvInputStream != null;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "mixed_formats.csv",
                "text/csv",
                csvInputStream
        );

        String[][] result = fileProcessingService.process(mockFile, Optional.empty());

        String[][] expected = {
                {"id", "amount", "date"},
                {"1", "1234.56", "2025-08-07"},
                {"2", "1234.56", "2025-07-08"},
                {"3", "1234.56", "2025-08-07"},
                {"4", "1234.56", "2025-08-07"},
                {"5", "1234.56", "2025-08-07"},
                {"6", "1234", "2025-08-07"},
                {"7", "1234.56", "2025-07-08"},
                {"8", "1234.56", "2007-08-25"},
                {"9", "1234.56", "2025-07-08"},
                {"10", "1234", "2025-08-07"},
        };

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testXlsxWithVariousNumberAndDateFormats() throws Exception {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("test/mixed_formats.xlsx");

        assert csvInputStream != null;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "mixed_formats.xlsx",
                ".xlsx",
                csvInputStream
        );

        String[][] result = fileProcessingService.process(mockFile, Optional.empty());

        String[][] expected = {
                {"id", "amount", "date"},
                {"1", "1234.56", "2025-08-07"},
                {"2", "1234.56", "2025-07-08"},
                {"3", "1234.56", "2025-08-07"},
                {"4", "1234.56", "2025-08-07"},
                {"5", "1234.56", "2025-08-07"},
                {"6", "1234", "2025-08-07"},
                {"7", "1234.56", "2025-07-08"},
                {"8", "1234.56", "2007-08-25"},
                {"9", "1234.56", "2025-07-08"},
                {"10", "1234", "2025-08-07"},
        };

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testCsvFile1() throws Exception {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("test/file_process_service_test_file_1.csv");

        assert csvInputStream != null;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "file_process_service_test_file_1.csv",
                "text/csv",
                csvInputStream
        );

        String[][] result = fileProcessingService.process(mockFile, Optional.empty());

        String[][] expected = {
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "Annual Returns on Investments in", "", "", "", "", ""},
                {"Year", "Stocks", "T.Bills", "T.Bonds", "", "", ""},
                {"1928", "0.4381", "0.0308", "0.0084", "", "", ""},
                {"1929", "-0.083", "0.0316", "0.042", "", "", ""},
                {"1930", "-0.2512", "0.0455", "0.0454", "", "", ""},
                {"", "stocks", "tbills", "bonds", "", "", ""},
                {"averages", "", "", "", "", "", ""}
        };

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testCsvFile2() throws Exception {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("test/file_process_service_test_file_2.csv");

        assert csvInputStream != null;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "file_process_service_test_file_1.csv",
                "text/csv",
                csvInputStream
        );

        String[][] result = fileProcessingService.process(mockFile, Optional.empty());

        String[][] expected = {
                {"Text", "123", "TRUE", "2020-01-01", "", "", "#DIV/0!"},
                {"String mit \"Quote\"", "456.789", "FALSE", "2024-12-31", "", "", "Fehler"},
                {"Leer", "", "TRUE", "", "456.889", "", ""}
        };

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testXlsxFile1() throws Exception {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("test/file_process_service_test_file_1.csv");

        assert csvInputStream != null;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "file_process_service_test_file_1.csv",
                ".xlsx",
                csvInputStream
        );

        String[][] result = fileProcessingService.process(mockFile, Optional.empty());

        String[][] expected = {
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "Annual Returns on Investments in", "", "", "", "", ""},
                {"Year", "Stocks", "T.Bills", "T.Bonds", "", "", ""},
                {"1928", "0.4381", "0.0308", "0.0084", "", "", ""},
                {"1929", "-0.083", "0.0316", "0.042", "", "", ""},
                {"1930", "-0.2512", "0.0455", "0.0454", "", "", ""},
                {"", "stocks", "tbills", "bonds", "", "", ""},
                {"averages", "", "", "", "", "", ""}
        };

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testXlsxFile2() throws Exception {
        InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("test/file_process_service_test_file_2.csv");

        assert csvInputStream != null;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "file_process_service_test_file_1.csv",
                ".xlsx",
                csvInputStream
        );

        String[][] result = fileProcessingService.process(mockFile, Optional.empty());

        String[][] expected = {
                {"Text", "123", "TRUE", "2020-01-01", "", "", "#DIV/0!"},
                {"String mit \"Quote\"", "456.789", "FALSE", "2024-12-31", "", "", "Fehler"},
                {"Leer", "", "TRUE", "", "456.889", "", ""}
        };

        assertThat(result).isEqualTo(expected);
    }
}
