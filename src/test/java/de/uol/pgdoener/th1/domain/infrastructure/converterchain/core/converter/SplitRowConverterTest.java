package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.SplitRowStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.SplitRowConverter;
import de.uol.pgdoener.th1.domain.converterchain.exception.ConverterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitRowConverterTest {

    @Test
    void testHandleRequest() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"},
                {"Footer1", "Footer2"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = {
                {"Header1", "Header2"},
                {"Row1", "Value1"},
                {"Row1", "Value2"},
                {"Row2", "Value3"},
                {"Row2", "Value4"},
                {"Footer1", "Footer2"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithoutHeaderFooter() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(0)
                .endRow(2);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = {
                {"Row1", "Value1"},
                {"Row1", "Value2"},
                {"Row2", "Value3"},
                {"Row2", "Value4"}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    void testHandleRequestWithEmptyMatrix() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(0)
                .endRow(0);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {{}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidStartRow() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(5)
                .endRow(10);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidEndRow() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(5);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidColumnIndex() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(5)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeStartRow() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(-1)
                .endRow(3);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeEndRow() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(-1);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeColumnIndex() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(-1)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNothingToSplit() {
        SplitRowStructureDto structure = new SplitRowStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitRowConverter converter = new SplitRowConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1"},
                {"Row2", "Value2\nValue3"},
                {"Footer1", "Footer2"}
        };

        String[][] result = converter.handleRequest(matrix);

        String[][] expected = {
                {"Header1", "Header2"},
                {"Row1", "Value1"},
                {"Row2", "Value2"},
                {"Row2", "Value3"},
                {"Footer1", "Footer2"}
        };
        assertArrayEquals(expected, result);
    }

}
