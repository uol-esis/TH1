package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveHeaderStructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveHeaderConverterTest {

    @Test
    void testHandleRequestWithDefaultValues() {
        // threshold = 2 (default), blackList = empty
        RemoveHeaderStructure removeHeaderStructure = new RemoveHeaderStructure(null, null);
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
    void testHandleRequestsLowerThreshold() {
        RemoveHeaderStructure removeHeaderStructure = new RemoveHeaderStructure(1, null);
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
        RemoveHeaderStructure removeHeaderStructure = new RemoveHeaderStructure(3, null);
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
        RemoveHeaderStructure structure = new RemoveHeaderStructure(null, null);
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
        RemoveHeaderStructure structure = new RemoveHeaderStructure(null, null);
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
    void testHandleRequestEmptyMatrix() {
        RemoveHeaderStructure structure = new RemoveHeaderStructure(null, null);
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);
        String[][] matrix = new String[][]{};

        String[][] result = converter.handleRequest(matrix);

        Assertions.assertEquals(0, result.length);
    }

    @Test
    void testHandleRequestNullValuesOnly() {
        RemoveHeaderStructure structure = new RemoveHeaderStructure(null, null);
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
        RemoveHeaderStructure structure = new RemoveHeaderStructure(null, new String[]{"a", "b", "c"});
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
    void testHeaderNotFoundDueToValidElementsMismatch() {
        RemoveHeaderStructure structure = new RemoveHeaderStructure(null, new String[]{"x", "y"});
        RemoveHeaderConverter converter = new RemoveHeaderConverter(structure);

        String[][] matrix = {
                {"a", "b", "c"},         // valid by default rules, but not in blackList
                {"d", "e", "f"}          // same
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

}
