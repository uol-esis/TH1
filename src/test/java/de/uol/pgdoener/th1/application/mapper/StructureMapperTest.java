package de.uol.pgdoener.th1.application.mapper;

import de.uol.pgdoener.th1.application.dto.ConverterTypeDto;
import de.uol.pgdoener.th1.application.dto.StructureDto;
import de.uol.pgdoener.th1.application.dto.StructureSummaryDto;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StructureMapperTest {

    @Test
    void testToDtoExhaustiveness() throws Exception {
        List<Class<?>> structureClasses = TestHelper.listEntityStructureClasses();
        for (Class<?> structureClass : structureClasses) {
            Structure structureInstance = (Structure) structureClass.getDeclaredConstructor().newInstance();
            TestHelper.populateFields(structureInstance);
            assertDoesNotThrow(() -> StructureMapper.toDto(structureInstance),
                    "StructureMapper.toDto should not throw an exception for " + structureClass.getSimpleName());
        }
    }

    @Test
    void testToDtoNonExistentStructure() {
        Structure nonExistentStructure = new Structure() {
            // This is a mock structure that does not match any known type
        };
        assertThrows(IllegalStateException.class, () -> StructureMapper.toDto(nonExistentStructure));
    }

    @Test
    void testToDtoOneConverterTypeAndClassPerStructure() throws Exception {
        List<Class<?>> structureClasses = TestHelper.listEntityStructureClasses();
        Set<StructureDto> converterTypeDtos = new HashSet<>();
        Set<Class<? extends StructureDto>> structureDtoClasses = new HashSet<>();
        for (Class<?> structureClass : structureClasses) {
            Structure structureInstance = (Structure) structureClass.getDeclaredConstructor().newInstance();
            TestHelper.populateFields(structureInstance);
            StructureDto structureDto = StructureMapper.toDto(structureInstance);
            assertTrue(converterTypeDtos.add(structureDto),
                    "StructureMapper.toDto should return a unique ConverterTypeDto for " + structureClass.getSimpleName());
            assertTrue(structureDtoClasses.add(structureDto.getClass()),
                    "StructureMapper.toDto should return a unique StructureDto class for " + structureClass.getSimpleName());
        }
        assertEquals(ConverterTypeDto.values().length - 1, converterTypeDtos.size()); // -1 because of the UNKNOWN_DEFAULT_OPEN_API enum value
        assertEquals(structureClasses.size(), structureDtoClasses.size());
    }

    // This is guarantied by the compiler, since StructureDto is sealed and all subclasses are known at compile time.
    @Test
    void testToEntityExhaustiveness() throws Exception {
        List<Class<?>> structureClasses = TestHelper.listDtoStructureClasses();
        for (Class<?> structureClass : structureClasses) {
            StructureDto structureDto = (StructureDto) structureClass.getDeclaredConstructor().newInstance();
            TestHelper.populateFields(structureDto);
            assertDoesNotThrow(() -> StructureMapper.toEntity(structureDto, 42, 12345L),
                    "StructureMapper.toEntity should not throw an exception for " + structureClass.getSimpleName());
        }
    }

    @Test
    void testToEntityOneConverterTypeAndClassPerStructure() throws Exception {
        List<Class<?>> structureClasses = TestHelper.listDtoStructureClasses();
        Set<Structure> structures = new HashSet<>();
        Set<Class<? extends Structure>> structureClassesSet = new HashSet<>();
        for (Class<?> structureClass : structureClasses) {
            StructureDto structureDto = (StructureDto) structureClass.getDeclaredConstructor().newInstance();
            TestHelper.populateFields(structureDto);
            Structure structure = StructureMapper.toEntity(structureDto, 42, 12345L);
            assertTrue(structures.add(structure),
                    "StructureMapper.toEntity should return a unique Structure for " + structureClass.getSimpleName());
            assertTrue(structureClassesSet.add(structure.getClass()),
                    "StructureMapper.toEntity should return a unique Structure class for " + structureClass.getSimpleName());
            assertEquals(42, structure.getPosition(), "Structure Position should be set correctly");
            assertEquals(12345L, structure.getTableStructureId(), "TableStructure ID should be set correctly");
        }
        assertEquals(ConverterTypeDto.values().length - 1, structures.size()); // -1 because of the UNKNOWN_DEFAULT_OPEN_API enum value
        assertEquals(structureClasses.size(), structureClassesSet.size());
    }

    @Test
    void testToSummaryDto() throws Exception {
        List<Class<?>> structureClasses = TestHelper.listEntityStructureClasses();
        for (Class<?> structureClass : structureClasses) {
            Structure structureInstance = (Structure) structureClass.getDeclaredConstructor().newInstance();
            StructureSummaryDto structureDto = StructureMapper.toSummaryDto(structureInstance);
            assertNotNull(structureDto, "StructureMapper.toSummaryDto should not return null for " + structureClass.getSimpleName());
        }
    }

    @Test
    void testToSummaryDtoWithInvalidStructure() {
        Structure invalidStructure = new Structure() {
            // This is a mock structure that does not match any known type
        };
        assertThrows(IllegalStateException.class, () -> StructureMapper.toSummaryDto(invalidStructure),
                "StructureMapper.toSummaryDto should throw IllegalStateException for invalid structure");
    }

}
