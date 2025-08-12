package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.SplitCellStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.exception.ConverterException;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.SplitCellConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitCellConverterTest {

    @Test
    void testHandleRequest() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitCellConverter converter = new SplitCellConverter(structure);
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
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(0)
                .endRow(2);
        SplitCellConverter converter = new SplitCellConverter(structure);
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
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(0)
                .endRow(0);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {{}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidStartRow() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(5)
                .endRow(10);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidEndRow() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(5);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithInvalidColumnIndex() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(5)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeStartRow() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(-1)
                .endRow(3);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeEndRow() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(-1);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNegativeColumnIndex() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(-1)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitCellConverter converter = new SplitCellConverter(structure);
        String[][] matrix = {
                {"Header1", "Header2"},
                {"Row1", "Value1\nValue2"},
                {"Row2", "Value3\nValue4"}
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestWithNothingToSplit() {
        SplitCellStructureDto structure = new SplitCellStructureDto()
                .columnIndex(1)
                .delimiter("\n")
                .startRow(1)
                .endRow(3);
        SplitCellConverter converter = new SplitCellConverter(structure);
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
