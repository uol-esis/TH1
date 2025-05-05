package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.data.entity.TableStructure;
import de.uol.pgdoener.th1.data.repository.DynamicTableRepository;
import de.uol.pgdoener.th1.data.repository.StructureRepository;
import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import de.uol.pgdoener.th1.metabase.MBService;
import de.uol.pgdoener.th1.metabase.MetabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConvertFileServiceTest {

    @Mock
    MBService mbService;
    @Mock
    TableStructureRepository tableStructureRepository;
    @Mock
    StructureRepository structureRepository;
    @Mock
    DynamicTableRepository dynamicTableRepository;

    ConvertFileService convertFileService;

    @BeforeEach
    void setUp() {
        convertFileService = new ConvertFileService(mbService, tableStructureRepository, structureRepository, dynamicTableRepository);
    }

    @Test
    void testConvertAndSaveInDBMetabase() {
        TableStructure tableStructure = new TableStructure(
                1L,
                "test",
                1,
                1
        );
        doThrow(MetabaseException.class).when(mbService).updateAllDatabases();
        when(tableStructureRepository.findById(1L)).thenReturn(Optional.of(tableStructure));
        when(structureRepository.findByTableStructureId(1L)).thenReturn(List.of());
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "test;test1".getBytes());

        assertThrows(MetabaseException.class, () -> convertFileService.convertAndSaveInDB(1L, file));
    }

}
