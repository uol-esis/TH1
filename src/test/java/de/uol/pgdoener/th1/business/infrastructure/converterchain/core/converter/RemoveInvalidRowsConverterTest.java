package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.dto.RemoveInvalidRowsStructureDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveInvalidRowsConverterTest {

    @Test
    void testHandleRequestWithDefaultValues() {
        // threshold = 2 (default), blackList = empty
        RemoveInvalidRowsStructureDto removeInvalidRowsStructure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(removeInvalidRowsStructure);
        String[][] matrix = new String[][]{
                {"Invalid", null, ""},
                {"Header1", "Header2", "Header3"},
                {"Invalid", null, ""},
                {"Data1", "Data2", "Data3"},
                {"Data4", "Data5", "Data6"},
                {"Invalid", "Invalid", ""},
                {"Data7", "Data8", "Data9"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                {"Header1", "Header2", "Header3"},
                {"Data1", "Data2", "Data3"},
                {"Data4", "Data5", "Data6"},
                {"Data7", "Data8", "Data9"},
        }, result);
    }

    @Test
    void testHandleRequestsLowerThreshold() {
        RemoveInvalidRowsStructureDto removeInvalidRowsStructure = new RemoveInvalidRowsStructureDto()
                .threshold(1)
                .blackList(List.of());
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(removeInvalidRowsStructure);
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
        RemoveInvalidRowsStructureDto removeInvalidRowsStructure = new RemoveInvalidRowsStructureDto()
                .threshold(3)
                .blackList(List.of());
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(removeInvalidRowsStructure);
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
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(structure);
        String[][] matrix = new String[][]{
                {null, "", ""},
                {null, "", null}
        };

        String[][] result = converter.handleRequest(matrix);

        // Wenn keine Header-Zeile gefunden wird, sollte Originalmatrix zurückgegeben werden
        assertArrayEquals(matrix, result);
    }

    @Test
    void testHandleRequestNoHeaderRowFoundWithValidElementsReturnsOriginal() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blackList(List.of());
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(structure);
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
    void testValidElementsOverridesDefaultValidation() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blackList(List.of("a", "b", "c"));
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(structure);

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
                        {"d", "e", "f"}},
                result
        );
    }

    @Test
    void testHeaderNotFoundDueToValidElementsMismatch() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blackList(List.of("x", "y"));
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(structure);

        String[][] matrix = {
                {"a", "b", "c"},         // valid by default rules, but not in blackList
                {"d", "e", "f"}          // same
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

}
