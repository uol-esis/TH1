package de.uol.pgdoener.th1.application.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.converterchain.converter.RemoveRowByIndexConverter;
import de.uol.pgdoener.th1.application.converterchain.exception.ConverterException;
import de.uol.pgdoener.th1.application.dto.RemoveRowByIndexStructureDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveRowByIndexConverterTest {

    @Test
    void testHandleRequest() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of(1));
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyIndexArray() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of());
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of());
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of(3));
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of(-1));
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMultipleRows() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of(0, 2));
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestMultipleRowsWithSameIndex() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of(1, 1));
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t", "e", "s", "t"}, {"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestMinimalMatrix() {
        RemoveRowByIndexStructureDto structure = new RemoveRowByIndexStructureDto(null, List.of(0));
        RemoveRowByIndexConverter converter = new RemoveRowByIndexConverter(structure);
        String[][] matrix = new String[][]{{"t"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));

    }

}
