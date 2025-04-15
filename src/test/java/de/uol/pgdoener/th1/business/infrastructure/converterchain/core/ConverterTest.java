package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConverterTest {

    @Mock
    Converter nextConverter;

    @Test
    void testHandleRequestNull() {
        Converter converter = new Converter() {
        };

        assertThrows(NullPointerException.class, () -> converter.handleRequest(null));
    }

    @Test
    void testHandleRequestEmptyMatrix() {
        Converter converter = new Converter() {
        };

        String[][] emptyMatrix = {};

        assertThrows(ConverterException.class, () -> converter.handleRequest(emptyMatrix));
    }

    @Test
    void testHandleRequestEmptyMatrix2() {
        Converter converter = new Converter() {
        };

        String[][] emptyMatrix = {{}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(emptyMatrix));
    }

    @Test
    void testHandleRequestEmptyMatrixWithNextConverter() {
        Converter converter = new Converter() {
        };
        converter.setNextHandler(nextConverter);

        String[][] emptyMatrix = {{}};

        assertThrows(ConverterException.class, () -> converter.handleRequest(emptyMatrix));
    }

    @Test
    void testHandleRequestWithNextConverter() throws Exception {
        Converter converter = new Converter() {
        };
        converter.setNextHandler(nextConverter);

        String[][] matrix = {{"data1", "data2"}, {"data3", "data4"}};
        converter.handleRequest(matrix);

        verify(nextConverter).handleRequest(matrix);
    }

    @Test
    @SuppressWarnings({"ConstantConditions", "WriteOnlyObject"})
    void testHandleRequestWithNextConverterNull() {
        Converter converter = new Converter() {
        };

        assertThrows(NullPointerException.class, () -> converter.setNextHandler(null));
    }

}
