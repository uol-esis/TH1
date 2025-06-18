package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.ReplaceEntriesStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ReplaceEntriesConverterTest {

    @Test
    void testHandleRequestSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .startColumn(0)
                .endColumn(2)
                .search("test")
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"TEST", "TEST", "test"}, {"word", "word", "word"}, {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestRegexSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .startColumn(0)
                .endColumn(2)
                .search(null)
                .replacement("TEST")
                .regexSearch(".*es.*");
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"TEST", "TEST", "test"}, {"word", "word", "word"}, {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .startColumn(0)
                .endColumn(2)
                .search("test")
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{}};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyBothSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .startColumn(0)
                .endColumn(2)
                .search("test")
                .replacement("TEST")
                .regexSearch(".*es.*");
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"TEST", "TEST", "test"}, {"word", "word", "word"}, {"a", "b", "c"}}, result);
    }

    @Test
    void testHandleRequestNoSearch() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .startColumn(0)
                .endColumn(2)
                .search(null)
                .replacement("TEST")
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestNoReplacement() {
        ReplaceEntriesStructureDto structure = new ReplaceEntriesStructureDto()
                .startRow(0)
                .endRow(2)
                .startColumn(0)
                .endColumn(2)
                .search("test")
                .replacement(null)
                .regexSearch(null);
        ReplaceEntriesConverter converter = new ReplaceEntriesConverter(structure);
        String[][] matrix = new String[][]{{"test", "test", "test"}, {"word", "word", "word"}, {"a", "b", "c"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

}
