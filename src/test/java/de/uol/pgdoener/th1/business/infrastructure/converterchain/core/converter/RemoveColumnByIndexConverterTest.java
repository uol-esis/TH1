package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveColumnByIndexStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveColumnByIndexConverterTest {

    @Test
    void testHandleRequest() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{2});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "t"}, {"w", "o", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, matrix);
    }

    @Test
    void testHandleRequestEmptyIndexArray() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, matrix);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(IndexOutOfBoundsException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));

        assertArrayEquals(new String[][]{}, matrix);
    }

    @Test
    void testHandleRequestIndexOutOfBounds() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{4});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, matrix);
        assertArrayEquals(matrix, result);
    }

    @Test
    void testHandleRequestMultipleColumns() {
        RemoveColumnByIndexStructure structure = new RemoveColumnByIndexStructure(new Integer[]{0, 1});
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"s", "t"}, {"r", "d"}}, result);
        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, matrix);
    }

}
