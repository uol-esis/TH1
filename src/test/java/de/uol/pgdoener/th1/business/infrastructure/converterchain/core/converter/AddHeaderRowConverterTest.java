package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.HeaderRowStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddHeaderRowConverterTest {

    @Test
    void testHandleRequest() throws Exception {
        HeaderRowStructure structure = new HeaderRowStructure(new String[]{"t", "e", "s", "t"});
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        HeaderRowStructure structure = new HeaderRowStructure(new String[]{"t", "e", "s", "t"});
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyHeaderRow() throws Exception {
        HeaderRowStructure structure = new HeaderRowStructure(new String[]{});
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestHeaderRowLongerThanMatrix() {
        HeaderRowStructure structure = new HeaderRowStructure(new String[]{"t", "e", "s", "t", "extra"});
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(IllegalArgumentException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMinimalMatrix() throws Exception {
        HeaderRowStructure structure = new HeaderRowStructure(new String[]{"t"});
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t"}}, result);
    }

}
