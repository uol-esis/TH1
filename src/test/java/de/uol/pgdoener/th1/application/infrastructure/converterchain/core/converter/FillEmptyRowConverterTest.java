package de.uol.pgdoener.th1.application.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.converterchain.converter.FillEmptyRowConverter;
import de.uol.pgdoener.th1.application.converterchain.exception.ConverterException;
import de.uol.pgdoener.th1.application.dto.FillEmptyRowStructureDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FillEmptyRowConverterTest {

    @Test
    void testHandleRequest() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(0));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "t", "s", "s"}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestWithEmptyValues() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(0));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"", "t", "", "s", ""}, {"", "w", "o", "r", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"", "t", "t", "s", "s"}, {"", "w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestMultipleRows() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(0, 1));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "t", "s", "s"}, {"w", "o", "o", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(0));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyIndexArray() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of());
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(3));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(-1));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMinimalMatrix() {
        FillEmptyRowStructureDto structure = new FillEmptyRowStructureDto(null, List.of(0));
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t"}}, result);
    }

}
