package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveFooterStructureDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveFooterConverterTest {

    @Test
    void testHandleRequestWithDefaultValues() {
        // threshold = 2 (default), blackList = empty
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);

        String[][] matrix = {
                {"Data1", "Data2", "Data3"},
                {"Data4", "Data5", "Data6"},
                {"Data7", "Data8", "Data9"},
                {"Data10", "Data11", "Data12"},
                {"Noise", "", ""},
                {"Footer1", "Footer2", null}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"Data1", "Data2", "Data3"},
                {"Data4", "Data5", "Data6"},
                {"Data7", "Data8", "Data9"},
                {"Data10", "Data11", "Data12"},
        }, result);
    }

    @Test
    void testHandleRequestsLowerThreshold() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(1)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);
        String[][] matrix = new String[][]{
                {"Header1", "Header2", "Header3"},   // valid header (2 valid entries)
                {"Data1", "Data2", "Data3"},  // should be kept
                {"Invalid", null, ""},        // not valid
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"Header1", "Header2", "Header3"},  // valid header (2 valid entries)
                {"Data1", "Data2", "Data3"}}, result);
    }

    @Test
    void testHandleRequestsHigherThreshold() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(3)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);
        String[][] matrix = new String[][]{
                {"Header1", "Header2", "Header3", "Header4"},
                {"Data1", "Data2", "Data3", "Data4"},// valid header (2 valid entries)
                {"Invalid", "Invalid", "Invalid",}  // should be kept
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"Header1", "Header2", "Header3", "Header4"},
                {"Data1", "Data2", "Data3", "Data4"}}, result);
    }


    @Test
    void testHandleRequestWithNoValidElementsNoHeaderRowFound() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);
        String[][] matrix = new String[][]{
                {null, "", ""},      // not valid
                {null, "", null}     // not valid
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

    @Test
    void testHandleRequestWithValidElementsNoHeaderRowFound() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);
        String[][] matrix = new String[][]{
                {"skip", "this"},
                {"Header", "Valid"},
                {"Row1", "Data1"},
                {"Row2", "Data2"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);
        String[][] matrix = new String[][]{};

        String[][] result = converter.handleRequest(matrix);

        Assertions.assertEquals(0, result.length);
    }

    @Test
    void testHandleRequestNullValuesOnly() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);
        String[][] matrix = new String[][]{
                {null, null, null},
                {null, null, null}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

    @Test
    void testValidElementsOverridesDefaultValidation() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of("a", "b", "c"));
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);

        String[][] matrix = {
                {"a", "b", "d"},
                {"a", "x", "d"},
                {"x", "y", "z"},                // none valid
                {"a", "*", null},              // 1 valid (below threshold)
                {"a", "b", "c"},
                {"a", "b", "d"},
                {"a", "x", "d"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                        {"a", "b", "d"},
                        {"a", "x", "d"},
                        {"x", "y", "z"},                // none valid
                }, result
        );
    }

    @Test
    void testHeaderNotFoundDueToValidElementsMismatch() {
        RemoveFooterStructureDto structure = new RemoveFooterStructureDto()
                .threshold(null)
                .blackList(List.of("x", "y"));
        RemoveFooterConverter converter = new RemoveFooterConverter(structure);

        String[][] matrix = {
                {"a", "b", "c"},         // valid by default rules, but not in blackList
                {"d", "e", "f"}          // same
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

}
