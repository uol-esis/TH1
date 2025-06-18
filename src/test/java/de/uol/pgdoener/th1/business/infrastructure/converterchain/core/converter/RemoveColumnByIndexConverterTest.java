package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveColumnByIndexStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveColumnByIndexConverterTest {

    @Test
    void testHandleRequest() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of(2));
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "t"}, {"w", "o", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyIndexArray() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of());
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of());
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of(4));
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        assertThrows(ConverterException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of(-1));
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        assertThrows(ConverterException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMultipleColumns() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of(0, 1));
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"s", "t"}, {"r", "d"}}, result);
    }

    @Test
    void testHandleRequestDuplicateColumns() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of(1, 1));
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}};

        String[][] result = removeColumnByIndexConverter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "s", "t"}, {"w", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestMinimalMatrix() {
        RemoveColumnByIndexStructureDto structure = new RemoveColumnByIndexStructureDto(null, List.of(0));
        RemoveColumnByIndexConverter removeColumnByIndexConverter = new RemoveColumnByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t"}};

        assertThrows(ConverterException.class, () -> removeColumnByIndexConverter.handleRequest(matrix));
    }

}
