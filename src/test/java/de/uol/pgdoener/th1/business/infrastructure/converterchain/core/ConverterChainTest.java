package de.uol.pgdoener.th1.business.infrastructure.converterchain.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConverterChainTest {

    @Mock
    Converter converter;

    @Mock
    Converter secondConverter;

    @Mock
    Converter thirdConverter;

    @Test
    void testGetFirst() {
        ConverterChain converterChain = new ConverterChain();
        assertNull(converterChain.getFirst());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testNullConverter() {
        ConverterChain converterChain = new ConverterChain();
        assertThrows(NullPointerException.class, () -> converterChain.add(null));
    }

    @Test
    void testAdd() {
        ConverterChain converterChain = new ConverterChain();
        converterChain.add(converter);

        assertEquals(converter, converterChain.getFirst());
        verify(converter, never()).setNextHandler(any());
    }

    @Test
    void testAddMultipleConverters() {
        ConverterChain converterChain = new ConverterChain();
        converterChain.add(converter);
        converterChain.add(secondConverter);

        assertEquals(converter, converterChain.getFirst());
        verify(converter).setNextHandler(secondConverter);
        verify(secondConverter, never()).setNextHandler(any());

        converterChain.add(thirdConverter);
        assertEquals(converter, converterChain.getFirst());
        verify(secondConverter).setNextHandler(thirdConverter);
        verify(thirdConverter, never()).setNextHandler(any());
    }

}
