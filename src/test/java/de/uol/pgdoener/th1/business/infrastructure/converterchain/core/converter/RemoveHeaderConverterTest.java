package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveHeaderStructureDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveHeaderConverterTest {

    @Test
    void testHandleRequestWithDefaultValues() {
        // threshold = 2 (default), blackList = empty
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(removeHeaderStructure);
        String[][] matrix = new String[][]{
                {"Invalid", null, ""},       // not valid
                {"Header1", "Header2", ""},  // not valid header (2 valid entries)
                {"Data1", "Data2", "Data3"}  // should be kept
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"Data1", "Data2", "Data3"}}, result);
    }

    @Test
    void testHandleRequestWithDefaultValuesAndSmallTable() {
        RemoveHeaderStructureDto removeHeaderStructureDto = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(removeHeaderStructureDto);
        String[][] matrix = new String[][]{
                {"Invalid", null},
                {"Invalid", ""},
                {"Header1", "Header2"},
                {"Data1", "Data2"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"Header1", "Header2"},
                {"Data1", "Data2"},
                {"Data3", "Data4"},
        }, result);
    }

    @Test
    void testHandleRequestsLowerThreshold() {
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto()
                .threshold(1)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(removeHeaderStructure);
        String[][] matrix = new String[][]{
                {"Invalid", null, ""},       // not valid
                {"Header1", "Header2", ""},  // valid header (2 valid entries)
                {"Data1", "Data2", "Data3"}  // should be kept
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"Header1", "Header2", ""}, {"Data1", "Data2", "Data3"}}, result);
    }

    @Test
    void testHandleRequestsHigherThreshold() {
        RemoveHeaderStructureDto removeHeaderStructure = new RemoveHeaderStructureDto()
                .threshold(3)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(removeHeaderStructure);
        String[][] matrix = new String[][]{
                {"Invalid", null, "", ""},       // not valid
                {"Header1", "Header2", "", ""}, // not valid header (2 valid entries)
                {"Header2", "Header3", "Header4", ""},  // not valid header (3 valid entries)
                {"Data1", "Data2", "Data3", "Data4"}  // should be kept
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"Data1", "Data2", "Data3", "Data4"}}, result);
    }


    @Test
    void testHandleRequestNoHeaderRowFoundWithNotValidElementsReturnsOriginal() {
        RemoveHeaderStructureDto structure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);
        String[][] matrix = new String[][]{
                {null, "", ""},      // not valid
                {null, "", null}     // not valid
        };

        String[][] result = converter.handleRequest(matrix);

        // Wenn keine Header-Zeile gefunden wird, sollte Originalmatrix zurückgegeben werden
        assertArrayEquals(matrix, result);
    }

    @Test
    void testHandleRequestNoHeaderRowFoundWithValidElementsReturnsOriginal() {
        RemoveHeaderStructureDto structure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);
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
    void testHandleRequestNullValuesOnly() {
        RemoveHeaderStructureDto structure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of());
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);
        String[][] matrix = new String[][]{
                {null, null, null},
                {null, null, null}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

    @Test
    void testValidElementsOverridesDefaultValidation() {
        RemoveHeaderStructureDto structure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of("a", "b", "c"));
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);

        String[][] matrix = {
                {"a", "b", "d"},
                {"a", "x", "d"},
                {"x", "y", "z"},
                {"a", "*", null},              // 1 valid (below threshold)
                {"a", "b", "c"},               // 3 valid → header row
                {"d", "e", "f"}                // to remain
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                        {"x", "y", "z"},
                        {"a", "*", null},              // 1 valid (below threshold)
                        {"a", "b", "c"},               // 3 valid → header row
                        {"d", "e", "f"}},
                result
        );
    }

    @Test
    void testValidElementsOverridesBlocklistWithSmallTable() {
        RemoveHeaderStructureDto structure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of("a", "b", "c"));
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);

        String[][] matrix = {
                {"a", "b"},
                {"a", "x"},
                {"x", "y"}, //HEADER
                {"a", "*"},
                {"a", "b"},
                {"d", "e"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                        {"x", "y"},
                        {"a", "*"},
                        {"a", "b"},
                        {"d", "e"}},
                result
        );
    }

    @Test
    void testHeaderNotFoundDueToValidElementsMismatch() {
        RemoveHeaderStructureDto structure = new RemoveHeaderStructureDto()
                .threshold(null)
                .blockList(List.of("x", "y"));
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);

        String[][] matrix = {
                {"a", "b", "c"},         // valid by default rules, but not in blackList
                {"d", "e", "f"}          // same
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

}
