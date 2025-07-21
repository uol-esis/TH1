package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.RemoveGroupedHeaderStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.RemoveGroupedHeaderConverter;
import de.uol.pgdoener.th1.domain.converterchain.exception.ConverterException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveGroupedHeaderConverterTest {

    @Test
    void testHandleRequest() {
        RemoveGroupedHeaderStructureDto structure = new RemoveGroupedHeaderStructureDto(
                null,
                List.of(0, 1),
                List.of(0)
        )
                .startRow(3)
                .startColumn(null);
        RemoveGroupedHeaderConverter converter = new RemoveGroupedHeaderConverter(structure);
        String[][] input = new String[][]{
                {"test", "test1", "test2", "test2"},
                {"hello", "hello2", "hello3", "hello4"},
                {"stuff", "", "", ""},
                {"s1", "1", "2", "3"},
                {"s2", "4", "5", "6"},
        };

        String[][] output = converter.handleRequest(input);

        assertArrayEquals(new String[][]{
                {"undefined", "undefined", "undefined", "undefined"},
                {"s1", "test1", "hello2", "1"},
                {"s1", "test2", "hello3", "2"},
                {"s1", "test2", "hello4", "3"},
                {"s2", "test1", "hello2", "4"},
                {"s2", "test2", "hello3", "5"},
                {"s2", "test2", "hello4", "6"}
        }, output);
    }

    @Test
    void testHandleRequestHigherColumnIndex() {
        RemoveGroupedHeaderStructureDto structure = new RemoveGroupedHeaderStructureDto(
                null,
                List.of(0, 1),
                List.of(1)
        )
                .startRow(3)
                .startColumn(null);
        RemoveGroupedHeaderConverter converter = new RemoveGroupedHeaderConverter(structure);
        String[][] input = new String[][]{
                {"test-1", "test", "test1", "test2", "test2"},
                {"hello-1", "hello", "hello2", "hello3", "hello4"},
                {"stuff-1", "stuff", "", "", ""},
                {"s", "s1", "1", "2", "3"},
                {"s", "s2", "4", "5", "6"},
        };

        String[][] output = converter.handleRequest(input);

        assertArrayEquals(new String[][]{
                {"undefined", "undefined", "undefined", "undefined"},
                {"s1", "test1", "hello2", "1"},
                {"s1", "test2", "hello3", "2"},
                {"s1", "test2", "hello4", "3"},
                {"s2", "test1", "hello2", "4"},
                {"s2", "test2", "hello3", "5"},
                {"s2", "test2", "hello4", "6"}
        }, output);
    }

    @Test
    void testHandleRequestWithRowsAboveHeader() {
        RemoveGroupedHeaderStructureDto structure = new RemoveGroupedHeaderStructureDto(
                null,
                List.of(1, 2),
                List.of(0)
        )
                .startRow(3)
                .startColumn(null);
        RemoveGroupedHeaderConverter converter = new RemoveGroupedHeaderConverter(structure);
        String[][] input = new String[][]{
                {"header", "header1", "header2", "header3"},
                {"test", "test1", "test2", "test2"},
                {"hello", "hello2", "hello3", "hello4"},
                {"stuff", "", "", ""},
                {"s1", "1", "2", "3"},
                {"s2", "4", "5", "6"},
        };

        assertThrows(ConverterException.class, () -> converter.handleRequest(input));
    }

    @Test
    void testHandleRequestMultipleColumnIndices() {
        RemoveGroupedHeaderStructureDto structure = new RemoveGroupedHeaderStructureDto(
                null,
                List.of(0, 1),
                List.of(0, 1)
        )
                .startRow(3)
                .startColumn(null);
        RemoveGroupedHeaderConverter converter = new RemoveGroupedHeaderConverter(structure);
        String[][] input = new String[][]{
                {"test-1", "test", "test1", "test2", "test2"},
                {"hello-1", "hello", "hello2", "hello3", "hello4"},
                {"stuff-1", "stuff", "", "", ""},
                {"s", "s1", "1", "2", "3"},
                {"s-1", "s2", "4", "5", "6"},
        };

        String[][] output = converter.handleRequest(input);

        assertArrayEquals(new String[][]{
                {"undefined", "undefined", "undefined", "undefined", "undefined"},
                {"s", "s1", "test1", "hello2", "1"},
                {"s", "s1", "test2", "hello3", "2"},
                {"s", "s1", "test2", "hello4", "3"},
                {"s-1", "s2", "test1", "hello2", "4"},
                {"s-1", "s2", "test2", "hello3", "5"},
                {"s-1", "s2", "test2", "hello4", "6"}
        }, output);
    }

}
