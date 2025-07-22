package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.RemoveTrailingColumnStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.RemoveTrailingColumnConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveTrailingColumnConverterTest {


    @Test
    void testNoTrailingMatrixToRemove() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto().blockList(List.of());
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);
        String[][] matrix = new String[][]{
                {"test", "", "test1"},
                {"test", "", "test1"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(
                new String[][]{
                        {"test", "", "test1"},
                        {"test", "", "test1"}
                }, result);
    }

    @Test
    void testRemoveSingleTrailingEmptyColumn() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto().blockList(List.of());
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);
        String[][] matrix = new String[][]{
                {"Header1", "Header2", "Header3", "Header4", ""},
                {"Data1", "Data2", "", "", ""},
                {"Data3", "Data4", "", "Data5", ""},
                {"", "Data6", "Data7", "", ""},
                {"Data8", "", "", "", ""},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(
                new String[][]{
                        {"Header1", "Header2", "Header3", "Header4"},
                        {"Data1", "Data2", "", ""},
                        {"Data3", "Data4", "", "Data5"},
                        {"", "Data6", "Data7", ""},
                        {"Data8", "", "", ""},
                }, result);
    }

    @Test
    void testRemoveMultipleTrailingEmptyColumns() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto().blockList(List.of());
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);

        String[][] matrix = {
                {"Header1", "Header2", "", "", ""},
                {"Data1", "Data2", "", "", ""},
                {"Data3", "Data4", "", "", ""},
        };

        String[][] expected = {
                {"Header1", "Header2"},
                {"Data1", "Data2"},
                {"Data3", "Data4"},
        };

        String[][] result = converter.handleRequest(matrix);
        assertArrayEquals(expected, result);
    }

    @Test
    void testRemoveTrailingColumnsWithMixedValuesWithoutBlacklist() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto().blockList(List.of());
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);

        String[][] input = {
                {"A", "B", "*", "", null},
                {"1", "2", "3", "", ""},
                {"X", "", "*", "", ""},
        };

        String[][] expected = {
                {"A", "B", "*"},
                {"1", "2", "3"},
                {"X", "", "*"}
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(expected, result, "Matrix rows were not properly truncated based on valid values.");
    }

    @Test
    void testRemoveTrailingColumnsConsideringBlacklist() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto()
                .blockList(List.of("*"));
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);

        String[][] input = {
                {"A", "B", "*", "*", ""},
                {"1", "2", "3", "", ""},
                {"X", "", "*", "", ""}
        };

        String[][] expected = {
                {"A", "B", "*"},
                {"1", "2", "3"},
                {"X", "", "*"}
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(expected, result, "Blacklist entries were not excluded correctly.");
    }

    @Test
    void testRemoveTrailingColumnsWithPartialBlacklistMatch() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto()
                .blockList(List.of("*"));
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);

        String[][] input = {
                {"A", "B", "C", "C*D", "*"},
                {"1", "2", "3", "*", ""},
                {"X", "", "*", "", ""}
        };

        String[][] expected = {
                {"A", "B", "C", "C*D"},
                {"1", "2", "3", "*"},
                {"X", "", "*", ""}
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(expected, result);
    }


    @Test
    void testReturnOriginalMatrixIfNoValidElementExist() {
        RemoveTrailingColumnStructureDto structureDto = new RemoveTrailingColumnStructureDto()
                .blockList(List.of("*"));
        RemoveTrailingColumnConverter converter = new RemoveTrailingColumnConverter(structureDto);

        String[][] input = {
                {"*", "*", "*"},
                {"*", "", null}
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(input, result, "Should return original matrix if no valid elements found.");
    }
}
