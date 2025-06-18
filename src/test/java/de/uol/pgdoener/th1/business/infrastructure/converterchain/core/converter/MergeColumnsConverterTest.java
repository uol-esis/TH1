package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.MergeColumnsStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MergeColumnsConverterTest {

    @Test
    void testHandleRequest() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "", "r", "d"},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"merged", "h3", "h4"},
                {"w", "r", "d"},
                {"o", "r", "d"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithEmptyMatrix() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithPrecedenceOrder() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                "merged"
        )
                .precedenceOrder(List.of(1, 0));
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "f", "r", "d"},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"merged", "h3", "h4"},
                {"f", "r", "d"},
                {"o", "r", "d"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithInvalidPrecedenceOrder() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                "merged"
        )
                .precedenceOrder(List.of(2, 3));
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "f", "r", "d"},
                {" ", "o", "r", "d"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidColumnCount() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "f", "r", "d"},
                {" ", "o", "r", "d"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithEmptyHeaderName() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                ""
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "f", "r", "d"},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"", "h3", "h4"},
                {"w", "r", "d"},
                {"o", "r", "d"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestAtEndOfMatrix() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(2, 3),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "f", "", "d"},
                {"w", "o", "", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"h1", "h2", "merged"},
                {"w", "f", "d"},
                {"w", "o", "d"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestSpreadOut() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 2),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "", "r", "d"},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"merged", "h2", "h4"},
                {"w", "", "d"},
                {"r", "o", "d"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithManyColumns() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1, 2, 3),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "", "r", "d"},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"merged"},
                {"w"},
                {"o"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithIncompletePrecedenceOrder() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                "merged"
        )
                .precedenceOrder(List.of(1));
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"w", "", "r", "d"},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"merged", "h3", "h4"},
                {"w", "r", "d"},
                {"o", "r", "d"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithEmptyRow() {
        MergeColumnsStructureDto structure = new MergeColumnsStructureDto(
                null,
                List.of(0, 1),
                "merged"
        );
        MergeColumnsConverter converter = new MergeColumnsConverter(structure);
        String[][] matrix = new String[][]{
                {"h1", "h2", "h3", "h4"},
                {"", "", "", ""},
                {" ", "o", "r", "d"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = new String[][]{
                {"merged", "h3", "h4"},
                {"", "", ""},
                {"o", "r", "d"}
        };
        assertArrayEquals(expected, result);
    }

}
