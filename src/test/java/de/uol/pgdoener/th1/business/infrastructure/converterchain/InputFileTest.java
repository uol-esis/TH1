package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.business.dto.StructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputFileTest {

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testConstructor() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "test".getBytes());
        TableStructureDto tableStructureDto = defaultTableStructureDto();
        assertThrows(NullPointerException.class, () -> new InputFile(null, tableStructureDto));
        assertThrows(NullPointerException.class, () -> new InputFile(file, null));
        assertThrows(NullPointerException.class, () -> new InputFile(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"csv", "xlsx", "xls"})
    void testAsStringArray(String extension) throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test." + extension, "", getInputStream("/unit/test." + extension));
        TableStructureDto tableStructure = defaultTableStructureDto();
        InputFile inputFile = new InputFile(file, tableStructure);

        String[][] result = inputFile.asStringArray();

        System.out.println(Arrays.deepToString(result));

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"csv", "xlsx", "xls"})
    void testEmptyAsStringArray(String extension) throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test." + extension, "", getInputStream("/unit/empty." + extension));
        TableStructureDto tableStructure = defaultTableStructureDto();
        InputFile inputFile = new InputFile(file, tableStructure);

        String[][] result = inputFile.asStringArray();

        assertArrayEquals(new String[0][0], result);
    }

    TableStructureDto defaultTableStructureDto() {
        return new TableStructureDto()
                .name("test")
                .delimiter(";")
                .structures(List.of(
                        new StructureDto()
                                .converterType(ConverterTypeDto.REMOVE_ROW_BY_INDEX)
                                .rowIndex(List.of(1))
                ));
    }

    InputStream getInputStream(String path) {
        return getClass().getResourceAsStream(path);
    }

}
