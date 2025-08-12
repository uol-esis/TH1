package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.RemoveInvalidRowsStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.RemoveInvalidRowsConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveInvalidRowsConverterTest {

    @Test
    void testHandleRequestWithDefaultValues() {
        // threshold = 2 (default), blockList = empty
        RemoveInvalidRowsStructureDto removeInvalidRowsStructure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of());
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
    void testHandleRequestWithDefaultValuesAndSmallTable() {
        // threshold = 2 (default), blockList = empty
        RemoveInvalidRowsStructureDto removeInvalidRowsStructure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of());
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(removeInvalidRowsStructure);
        String[][] matrix = new String[][]{
                {"Invalid", null},
                {"Invalid", ""},
                {"Header1", "Header2"},
                {"Invalid", null},
                {"Invalid", ""},
                {"Data1", "Data2"},
                {"Data3", "Data4"},
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
        RemoveInvalidRowsStructureDto removeInvalidRowsStructure = new RemoveInvalidRowsStructureDto()
                .threshold(1)
                .blockList(List.of());
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
                .blockList(List.of());
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
    void testHandleRequestNoInvalidRowFoundWithNotValidElementsReturnsOriginal() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of());
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
    void testHandleRequestNoInvalidRowFoundWithValidElementsReturnsOriginal() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of());
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
    void testValidElementsOverridesblockList() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of("a", "b", "c"));
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
    void testValidElementsOverridesblockListWithSmallTable() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of("a", "b", "c"));
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(structure);

        String[][] matrix = {
                {"a", "b"},
                {"a", "x"},
                {"x", "y"},
                {"a", "*"},
                {"a", "b"},
                {"d", "e"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{
                        {"x", "y"},
                        {"d", "e"}},
                result
        );
    }

    @Test
    void tesNoInvalidRowFoundAndOverridesblockListReturnsOriginal() {
        RemoveInvalidRowsStructureDto structure = new RemoveInvalidRowsStructureDto()
                .threshold(null)
                .blockList(List.of("x", "y"));
        RemoveInvalidRowsConverter converter = new RemoveInvalidRowsConverter(structure);

        String[][] matrix = {
                {"a", "b", "c"},         // valid by default rules, but not in blockList
                {"d", "e", "f"}          // same
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

}
