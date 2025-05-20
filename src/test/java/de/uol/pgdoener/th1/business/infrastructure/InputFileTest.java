package de.uol.pgdoener.th1.business.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputFileTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testConstructor() {
        assertThrows(NullPointerException.class, () -> new InputFile(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"csv", "xlsx", "xls"})
    void testAsStringArray(String extension) throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test." + extension, "", getInputStream("/unit/test." + extension));
        InputFile inputFile = new InputFile(file);

        String[][] result = inputFile.asStringArray();
        System.out.println(Arrays.deepToString(result));

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"csv", "xlsx", "xls"})
    void testEmptyAsStringArray(String extension) throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test." + extension, "", getInputStream("/unit/empty." + extension));
        InputFile inputFile = new InputFile(file);

        String[][] result = inputFile.asStringArray();

        assertArrayEquals(new String[0][0], result);
    }

    InputStream getInputStream(String path) {
        return getClass().getResourceAsStream(path);
    }

}

