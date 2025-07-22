package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.AddHeaderNameStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.AddHeaderRowConverter;
import de.uol.pgdoener.th1.domain.converterchain.exception.ConverterException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddHeaderRowConverterTest {

    @Test
    void testHandleRequest() {
        AddHeaderNameStructureDto structure = new AddHeaderNameStructureDto(null, List.of("t", "e", "s", "t"));
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t", "e", "s", "t"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        AddHeaderNameStructureDto structure = new AddHeaderNameStructureDto(null, List.of("t", "e", "s", "t"));
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestEmptyHeaderRow() {
        AddHeaderNameStructureDto structure = new AddHeaderNameStructureDto(null, List.of());
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}}, result);
    }

    @Test
    void testHandleRequestHeaderRowLongerThanMatrix() {
        AddHeaderNameStructureDto structure = new AddHeaderNameStructureDto(null, List.of("t", "e", "s", "t", "extra"));
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w", "o", "r", "d"}, {"a", "b", "c", "d"}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(matrix));
    }

    @Test
    void testHandleRequestMinimalMatrix() {
        AddHeaderNameStructureDto structure = new AddHeaderNameStructureDto(null, List.of("t"));
        AddHeaderRowConverter converter = new AddHeaderRowConverter(structure);
        String[][] matrix = new String[][]{{"w"}};

        String[][] result = converter.handleRequest(matrix);

        assertArrayEquals(new String[][]{{"t"}}, result);
    }

}
