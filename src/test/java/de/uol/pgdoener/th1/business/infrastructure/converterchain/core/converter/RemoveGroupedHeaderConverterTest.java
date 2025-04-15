package de.uol.pgdoener.th1.business.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.structures.RemoveGroupedHeaderStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RemoveGroupedHeaderConverterTest {

    @Test
    void testHandleRequest() throws Exception {
        RemoveGroupedHeaderStructure structure = new RemoveGroupedHeaderStructure(
                new Integer[]{0},
                new Integer[]{0, 1},
                3,
                null
        );
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

}
