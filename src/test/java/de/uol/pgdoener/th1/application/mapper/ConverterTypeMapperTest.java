package de.uol.pgdoener.th1.application.mapper;

import de.uol.pgdoener.th1.application.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTypeMapperTest {

    @Test
    void testToDtoExhaustiveness() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class<?>> structureClasses = TestHelper.listStructureClasses();
        for (Class<?> structureClass : structureClasses) {
            Structure structureInstance = (Structure) structureClass.getDeclaredConstructor().newInstance();
            assertDoesNotThrow(() -> ConverterTypeMapper.toDto(structureInstance),
                    "ConverterTypeMapper.toDto should not throw an exception for " + structureClass.getSimpleName());
        }
    }

    @Test
    void testNonExistentStructure() {
        Structure nonExistentStructure = new Structure() {
            // This is a mock structure that does not match any known type
        };
        assertThrows(IllegalStateException.class, () -> ConverterTypeMapper.toDto(nonExistentStructure));
    }

    @Test
    void testOneConverterTypePerStructure() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class<?>> structureClasses = TestHelper.listStructureClasses();
        Set<ConverterTypeDto> converterTypeDtos = EnumSet.noneOf(ConverterTypeDto.class);
        for (Class<?> structureClass : structureClasses) {
            Structure structureInstance = (Structure) structureClass.getDeclaredConstructor().newInstance();
            assertTrue(converterTypeDtos.add(ConverterTypeMapper.toDto(structureInstance)),
                    "ConverterTypeMapper.toDto should return a unique ConverterTypeDto for " + structureClass.getSimpleName());
        }
        assertEquals(ConverterTypeDto.values().length - 1, converterTypeDtos.size()); // -1 because of the UNKNOWN_DEFAULT_OPEN_API enum value
    }

}
