package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.FillEmptyRowStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FillEmptyRowConverterTest {

    @Test
    void testHandleRequest() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "t", "s", "s"}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestMultipleRows() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0, 1});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "t", "s", "s"}, {"w", "o", "o", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyIndexArray() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestIndexOutOfBoundsPositive() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{3});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{-1});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMultipleRowsWithEmptyValues() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0, 1});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"", "", "", ""}, {"", "o", "", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMinimalMatrix() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t"}}, result);
    }

}
