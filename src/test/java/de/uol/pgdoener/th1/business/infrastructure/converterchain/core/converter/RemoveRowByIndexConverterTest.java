package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveRowByIndexStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveRowByIndexConverterTest {

    @Test
    void testHandleRequest() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{1});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, matrix);
    }

    @Test
    void testHandleRequestEmptyIndexArray() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, matrix);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(IndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));

        assertArrayEquals(new String[][]{}, matrix);
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{3});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(IllegalArgumentException.class, () -> converter.handleRequest(matrix));

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, matrix);
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{-1});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(IllegalArgumentException.class, () -> converter.handleRequest(matrix));

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, matrix);
    }

    @Test
    void testHandleRequestMultipleRows() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{0, 2});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"w", "o", "r", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, matrix);
    }

    @Test
    void testHandleRequestMultipleRowsWithSameIndex() {
        RemoveRowByIndexStructure structure = new RemoveRowByIndexStructure(new Integer[]{1, 1});
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, matrix);
    }

}
