package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.FillEmptyRowStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FillEmptyRowConverterTest {

    @Test
    void testHandleRequest() throws Exception {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "t", "s", "s"}, {"w", "o", "r", "d"}}, result);
    }

    @Test
    void testHandleRequestMultipleRows() throws Exception {
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

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyIndexArray() throws Exception {
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

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestIndexOutOfBoundsNegative() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{-1});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"t", "", "s", ""}, {"w", "o", "r", "d"}};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMultipleRowsWithEmptyValues() {
        FillEmptyRowStructure structure = new FillEmptyRowStructure(new Integer[]{0, 1});
        FillEmptyRowConverter converter = new FillEmptyRowConverter(structure);
        String[][] matrix = new String[][]{{"", "", "", ""}, {"", "o", "", "d"}};

        assertThrows(IllegalArgumentException.class, () -> converter.handleRequest(matrix));
    }

}
