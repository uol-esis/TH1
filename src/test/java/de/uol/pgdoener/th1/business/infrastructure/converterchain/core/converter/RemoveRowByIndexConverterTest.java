package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveRowByIndexStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveRowByIndexConverterTest {

    @Test
    void testHandleRequest() throws Exception {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{1});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyIndexArray() throws Exception {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(IndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{3});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(IllegalArgumentException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{-1});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(IllegalArgumentException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMultipleRows() throws Exception {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{0, 2});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestMultipleRowsWithSameIndex() throws Exception {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{1, 1});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
    }

}
