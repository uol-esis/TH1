package de.uol.pgdoener.th1.application.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.converterchain.converter.RemoveLeadingColumnConverter;
import de.uol.pgdoener.th1.application.dto.RemoveLeadingColumnStructureDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveLeadingColumnConverterTest {

    @Test
    void testRemoveSingleLeadingEmptyColumn() {
        RemoveLeadingColumnStructureDto structureDto = new RemoveLeadingColumnStructureDto().blockList(List.of());
        RemoveLeadingColumnConverter converter = new RemoveLeadingColumnConverter(structureDto);
        String[][] matrix = new String[][]{
                {"", "Header", "Header", "Header"},
                {"", "Data", "Data", "Data"},
                {"", "Data", "Data", "Data"},
                {"", "Data", "Data", "Data"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(
                new String[][]{
                        {"Header", "Header", "Header"},
                        {"Data", "Data", "Data"},
                        {"Data", "Data", "Data"},
                        {"Data", "Data", "Data"},
                }, result);
    }

    @Test
    void testRemoveMultipleLeadingEmptyColumns() {
        RemoveLeadingColumnStructureDto structureDto = new RemoveLeadingColumnStructureDto().blockList(List.of());
        RemoveLeadingColumnConverter converter = new RemoveLeadingColumnConverter(structureDto);
        String[][] matrix = new String[][]{
                {"", "", "Header", "Header", "Header"},
                {"", "", "Data", "Data", "Data"},
                {"", "", "Data", "Data", "Data"},
                {"", "", "Data", "Data", "Data"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(
                new String[][]{
                        {"Header", "Header", "Header"},
                        {"Data", "Data", "Data"},
                        {"Data", "Data", "Data"},
                        {"Data", "Data", "Data"},
                }, result);
    }

    @Test
    void testRemoveLeadingColumnsWithMixedValuesWithoutBlacklist() {
        RemoveLeadingColumnStructureDto structureDto = new RemoveLeadingColumnStructureDto().blockList(List.of()); // No blacklist
        RemoveLeadingColumnConverter converter = new RemoveLeadingColumnConverter(structureDto);

        String[][] input = {
                {"", "*", null, "A", "B", "C"},
                {"", "", "", "1", "2", "3"},
                {"", "*", "", "X", "", "*"},
        };

        String[][] expected = {
                {"*", null, "A", "B", "C"},
                {"", "", "1", "2", "3"},
                {"*", "", "X", "", "*"},
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(expected, result);
    }

    @Test
    void testRemoveLeadingColumnsConsideringBlacklist() {
        RemoveLeadingColumnStructureDto structureDto = new RemoveLeadingColumnStructureDto()
                .blockList(List.of("*"));
        RemoveLeadingColumnConverter converter = new RemoveLeadingColumnConverter(structureDto);

        String[][] input = {
                {"", "*", null, "A", "B", "C"},
                {"", "", "", "1", "2", "3"},
                {"", "*", "", "X", "", "*"},
        };

        System.out.println(Arrays.deepToString(input));

        String[][] expected = {
                {"A", "B", "C"},
                {"1", "2", "3"},
                {"X", "", "*"},
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(expected, result, "Blacklist entries were not excluded correctly.");
    }

    @Test
    void testRemoveLeadingColumnsWithPartialBlacklistMatch() {
        RemoveLeadingColumnStructureDto structureDto = new RemoveLeadingColumnStructureDto()
                .blockList(List.of("*"));
        RemoveLeadingColumnConverter converter = new RemoveLeadingColumnConverter(structureDto);

        String[][] input = {
                {"", "*", "A*B", "A", "B", "C"},
                {"", "", "", "1", "2", "3"},
                {"", "*", "", "X", "", "*"},
        };

        String[][] expected = {
                {"A*B", "A", "B", "C"},
                {"", "1", "2", "3"},
                {"", "X", "", "*"},
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(expected, result, "Blacklist entries were not excluded correctly.");
    }

    @Test
    void testReturnOriginalMatrixIfNoValidElementsExist() {
        RemoveLeadingColumnStructureDto structureDto = new RemoveLeadingColumnStructureDto()
                .blockList(List.of("*"));
        RemoveLeadingColumnConverter converter = new RemoveLeadingColumnConverter(structureDto);

        String[][] input = {
                {"*", "*", "*"},
                {"*", "", null}
        };

        String[][] result = converter.handleRequest(input);
        assertArrayEquals(input, result);
    }

}
