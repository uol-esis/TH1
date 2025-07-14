package de.uol.pgdoener.th1.application.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.converterchain.converter.ReplaceEntriesConverter;
import de.uol.pgdoener.th1.application.converterchain.exception.ConverterException;
import de.uol.pgdoener.th1.application.dto.ReplaceEntriesStructureDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ReplaceEntriesConverterTest {

    @Test
    void testHandleRequestSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(null)
                .endRow(null)
                .columnIndex(List.of(0))
                .search("test")
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"test", "test", "test"},
                {"word", "word", "word"},
                {"test", "test", "test"},
                {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"TEST", "test", "test"},
                {"word", "word", "word"},
                {"TEST", "test", "test"},
                {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestWithEndRowSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(null)
                .endRow(2)
                .columnIndex(List.of(0))
                .search("test")
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"test", "test", "test"},
                {"word", "word", "word"},
                {"test", "test", "test"},
                {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"TEST", "test", "test"},
                {"word", "word", "word"},
                {"test", "test", "test"},
                {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestWithMultipleIndex() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(null)
                .endRow(null)
                .columnIndex(List.of(0, 1))
                .search("test")
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"test", "test", "test"},
                {"word", "word", "word"},
                {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"TEST", "TEST", "test"},
                {"word", "word", "word"},
                {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestRegexSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(null)
                .endRow(null)
                .columnIndex(List.of(0))
                .search(null)
                .replacement("TEST")
                .regexSearch(".*es.*");
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"test", "test", "test"},
                {"word", "word", "word"},
                {"test", "test", "test"},
                {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"TEST", "test", "test"},
                {"word", "word", "word"},
                {"TEST", "test", "test"},
                {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestRegexSearchWithEndRow() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(null)
                .endRow(2)
                .columnIndex(List.of(0, 1))
                .search(null)
                .replacement("TEST")
                .regexSearch(".*es.*");
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"test", "test", "test"},
                {"word", "word", "word"},
                {"test", "test", "test"},
                {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"HEADER", "HEADER", "HEADER"},
                {"TEST", "TEST", "test"},
                {"word", "word", "word"},
                {"test", "test", "test"},
                {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestEmptyBothSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(null)
                .endRow(null)
                .columnIndex(List.of(0))
                .search(null)
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"}, {"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .columnIndex(List.of(0))
                .search("test")
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{}};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestNoReplacement() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .columnIndex(List.of(0))
                .search("test")
                .replacement(null)
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{
                {"HEADER", "HEADER", "HEADER"}, {"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

}
