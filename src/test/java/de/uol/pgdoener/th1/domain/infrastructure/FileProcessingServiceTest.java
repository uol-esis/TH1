package de.uol.pgdoener.th1.domain.infrastructure;

import de.uol.pgdoener.th1.domain.fileprocessing.service.FileProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.InputStream;

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

        String[][] result = fileProcessingService.process(mockFile);

        String[][] expected = {
                {"id", "amount", "date"},
                {"1.00", "1234.56", "2025-08-07"},
                {"2.00", "1234.56", "2025-07-08"},
                {"3.00", "1234.56", "2025-08-07"},
                {"4.00", "1234.56", "2025-08-07"},
                {"5.00", "1234.56", "2025-08-07"},
                {"6.00", "1234.00", "07-Aug-2025"},// nooo
                {"7.00", "1234.56", "2025-07-08"},
                {"8.00", "1234.56", "2007-08-25"},
                {"9.00", "1234.56", "2025-07-08"},
                {"10.00", "1234.00", "2025-08-07"},
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

        String[][] result = fileProcessingService.process(mockFile);

        String[][] expected = {
                {"id", "amount", "date"},
                {"1.00", "1234.56", "2025-08-07"},
                {"2.00", "1234.56", "2025-07-08"},
                {"3.00", "1234.56", "2025-08-07"},
                {"4.00", "1234.56", "2025-08-07"},
                {"5.00", "1234.56", "2025-08-07"},
                {"6.00", "1234.00", "2025-08-07"},
                {"7.00", "1234.56", "2025-07-08"},
                {"8.00", "1234.56", "2007-08-25"},
                {"9.00", "1234.56", "2025-07-08"},
                {"10.00", "1234.00", "2025-08-07"},
        };

        assertThat(result).isEqualTo(expected);
    }
}
