package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.FillEmptyColumnStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FillEmptyColumnConverterTest {

    @Test
    void testHandleRequest() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0, 1));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"t", "e"},
                {"w", ""},
                {"", "r"},
                {"", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"t", "e"},
                {"w", "e"},
                {"w", "r"},
                {"w", "d"}
        }, result);
    }

    @Test
    void testHandleRequestOne() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(1));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"t", "e"},
                {"w", ""},
                {"", "r"},
                {"", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"t", "e"},
                {"w", "e"},
                {"", "r"},
                {"", "d"}
        }, result);
    }

    @Test
    void testHandleRequestChanging() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"t", "e"},
                {"", ""},
                {"w", "r"},
                {"", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"t", "e"},
                {"t", ""},
                {"w", "r"},
                {"w", "d"}
        }, result);
    }

    @Test
    void testHandleRequestFirstEmpty() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"", "e"},
                {"", ""},
                {"w", "r"},
                {"", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"", "e"},
                {"", ""},
                {"w", "r"},
                {"w", "d"}
        }, result);
    }

    @Test
    void testHandleRequestWithEmptyColumn() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"", "e"},
                {"", ""},
                {"", "r"},
                {"", "d"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithOutOfBoundsIndex() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0, 2));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"t", "e"},
                {"w", ""},
                {"", "r"},
                {"", "d"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeIndex() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(-1));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"t", "e"},
                {"w", ""},
                {"", "r"},
                {"", "d"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithEmptyMatrix() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithEmptyRow() {
        FillEmptyColumnStructureDto structure = new FillEmptyColumnStructureDto(null, List.of(0));
        FillEmptyColumnConverter converter = new FillEmptyColumnConverter(structure);
        String[][] matrix = new String[][]{
                {"", ""},
                {"", ""},
                {"", ""}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

}
