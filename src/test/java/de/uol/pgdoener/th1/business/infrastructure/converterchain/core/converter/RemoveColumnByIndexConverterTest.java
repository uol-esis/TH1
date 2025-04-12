package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveColumnByIndexStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveColumnByIndexConverterTest {

    @Test
    void testHandleRequest() throws Exception {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{2});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "t"}, {"w", "o", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyIndexArray() throws Exception {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(IndexOutOfBoundsException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{4});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        assertThrows(IllegalArgumentException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{-1});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        assertThrows(IllegalArgumentException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMultipleColumns() throws Exception {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{0, 1});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"s", "t"}, {"r", "d"}}, result);
    }

    @Test
    void testHandleRequestDuplicateColumns() throws Exception {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{1, 1});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "s", "t"}, {"w", "r", "d"}}, result);
    }

}
