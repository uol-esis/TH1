package de.uol.pgdoener.th1.business.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileTypeTest {

    @Test
    void testUnsupportedFileType() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.th1", "text/plain", "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> FileType.getType(mockMultipartFile));
    }

    @Test
    void testCSVWithContentType() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.th1", "text/csv", "test".getBytes());
        assertEquals(FileType.CSV, FileType.getType(mockMultipartFile));
    }

    @Test
    void testCSVWithFileExtension() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv", "text/plain", "test".getBytes());
        assertEquals(FileType.CSV, FileType.getType(mockMultipartFile));
    }

    @Test
    void testExcelOLE2WithFileExtension() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.xls", "text/plain", "test".getBytes());
        assertEquals(FileType.EXCEL_OLE2, FileType.getType(mockMultipartFile));
    }

    @Test
    void testExcelOOXMLWithFileExtension() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.xlsx", "text/plain", "test".getBytes());
        assertEquals(FileType.EXCEL_OOXML, FileType.getType(mockMultipartFile));
    }

    @Test
    void testNullContentType() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv", null, "test".getBytes());
        assertEquals(FileType.CSV, FileType.getType(mockMultipartFile));
    }

    @Test
    void testEmptyOriginalFilename() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "", "text/csv", "test".getBytes());
        assertEquals(FileType.CSV, FileType.getType(mockMultipartFile));
    }

    @Test
    void testEmptyContentType() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv", "", "test".getBytes());
        assertEquals(FileType.CSV, FileType.getType(mockMultipartFile));
    }

    @Test
    void testEmptyFileExtension() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test", "text/plain", "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> FileType.getType(mockMultipartFile));
    }

    @Test
    void testNullMultipartFile() {
        assertThrows(NullPointerException.class, () -> FileType.getType(null));
    }

    @Test
    void testOtherFileExtension() {
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> FileType.getType(mockMultipartFile1));

        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file", "test.th1csv", "text/plain", "test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> FileType.getType(mockMultipartFile2));
    }

}
