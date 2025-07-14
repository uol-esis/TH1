package de.uol.pgdoener.th1.application.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.converterchain.converter.RemoveKeywordsConverter;
import de.uol.pgdoener.th1.application.dto.RemoveKeywordsStructureDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RemoveKeywordsConverterTest {

    @Test
    void testRemoveRowsWithKeyword_IgnoreCaseContains() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true)
                .removeColumns(false)
                .ignoreCase(true)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS)
                .keywords(List.of("remove"));

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"Header1", "Header2"},
                {"keep1", "keep2"},
                {"remove this", "value"},
                {"data", "REMOVE"},
                {"keep3", "keep4"},
        };

        String[][] expected = {
                {"Header1", "Header2"},
                {"keep1", "keep2"},
                {"keep3", "keep4"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(expected, result);
    }

    @Test
    void testRemoveRowsWithKeyword_CaseSensitiveEquals() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true)
                .removeColumns(false)
                .ignoreCase(false)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.EQUALS)
                .keywords(List.of("remove"));

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"Header1", "Header2"},
                {"remove", "value"},
                {"REMOVE", "value"},
                {"keep", "keep"},
        };

        String[][] expected = {
                {"Header1", "Header2"},
                {"REMOVE", "value"},
                {"keep", "keep"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(expected, result);
    }

    @Test
    void testRemoveColumnsWithKeywordInHeader_IgnoreCaseEquals() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true) // irrelevant here
                .removeColumns(true)
                .ignoreCase(true)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.EQUALS)
                .keywords(List.of("secret"));

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"ID", "Name", "Secret"},
                {"1", "Alice", "secret123"},
                {"2", "Bob", "nothing"},
                {"3", "Carol", "secret456"}
        };

        String[][] expected = {
                {"ID", "Name"},
                {"1", "Alice"},
                {"2", "Bob"},
                {"3", "Carol"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(expected, result);
    }

    @Test
    void testOnlyRowsRemovedWhenHeaderHasNoKeyword_IgnoreCaseContains() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true)
                .removeColumns(true)
                .ignoreCase(true)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS)
                .keywords(List.of("x"));

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"A", "B", "C"},
                {"1", "x", "3"},
                {"x", "x", "x"},
                {"4", "5", "6"},
                {"7", "8", "x"}
        };

        // Header does not contain "x", so rows containing "x" removed
        String[][] expected = {
                {"A", "B", "C"},
                {"4", "5", "6"},
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(expected, result);
    }

    @Test
    void testOnlyColumnsRemovedWhenHeaderHasKeyword_CaseSensitiveContains() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true)
                .removeColumns(true)
                .ignoreCase(false)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS)
                .keywords(List.of("B")); // Matches header cell "B"

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"A", "B", "C"},
                {"1", "2", "3"},
                {"4", "5", "6"}
        };

        // Only column 1 ("B") should be removed
        String[][] expected = {
                {"A", "C"},
                {"1", "3"},
                {"4", "6"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(expected, result);
    }

    @Test
    void testNoChangesWhenNoKeywordsMatch() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true)
                .removeColumns(true)
                .ignoreCase(true)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS)
                .keywords(List.of("zzz")); // No match

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"Header1", "Header2"},
                {"value1", "value2"},
                {"value3", "value4"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(matrix, result);
    }

    @Test
    void testNullValuesHandledGracefully() {
        RemoveKeywordsStructureDto structure = new RemoveKeywordsStructureDto()
                .removeRows(true)
                .removeColumns(true)
                .ignoreCase(true)
                .matchType(RemoveKeywordsStructureDto.MatchTypeEnum.CONTAINS)
                .keywords(List.of("danger"));

        RemoveKeywordsConverter converter = new RemoveKeywordsConverter(structure);

        String[][] matrix = {
                {"safe", null, "ok"},
                {"danger", "something", null},
                {"null", "safe", "still safe"}
        };

        // Header has no match, so row-based filtering happens
        String[][] expected = {
                {"safe", null, "ok"},
                {"null", "safe", "still safe"}
        };

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(expected, result);
    }
}
