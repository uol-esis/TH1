package de.uol.pgdoener.th1.application.infrastructure.converterchain;

import de.uol.pgdoener.th1.application.converterchain.exception.ConverterException;
import de.uol.pgdoener.th1.application.converterchain.service.ConverterChainService;
import de.uol.pgdoener.th1.application.dto.FillEmptyRowStructureDto;
import de.uol.pgdoener.th1.application.dto.RemoveRowByIndexStructureDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.application.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.application.infrastructure.InputFile;
import de.uol.pgdoener.th1.application.infrastructure.exceptions.InputFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConverterChainServiceTest {

    @Mock
    InputFile inputFile;

    @Test
    void testConstructorNoStructures() {
        TableStructureDto tableStructure = new TableStructureDto();

        assertDoesNotThrow(() -> new ConverterChainService(tableStructure));
    }

    @Test
    void testConstructorWithStructures() {
        TableStructureDto tableStructure = new TableStructureDto();
        tableStructure.addStructuresItem(new FillEmptyRowStructureDto().addRowIndexItem(0));

        assertDoesNotThrow(() -> new ConverterChainService(tableStructure));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testConstructorWithNull() {
        assertThrows(NullPointerException.class, () -> new ConverterChainService(null));
    }

    @Test
    void testPerformTransformationNoStructures() {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(List.of("A", "B"), List.of("C", "D")), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationNoStructuresEmpty() {
        String[][] inputMatrix = {};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationNoStructuresEmpty2() {
        String[][] inputMatrix = {{}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(List.of()), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationStructures() {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        tableStructure.addStructuresItem(new RemoveRowByIndexStructureDto().addRowIndexItem(0));

        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(List.of("C", "D")), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationIOException() {
        when(inputFile.asStringArray()).thenThrow(new InputFileException("Test exception"));

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        assertThrows(InputFileException.class, () -> converterChainService.performTransformation(inputFile));
    }

    @Test
    void testPerformTransformationException() {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        // -1 is usually not possible at this point, but this is used to test the exception handling
        tableStructure.addStructuresItem(new FillEmptyRowStructureDto().addRowIndexItem(-1));

        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterException e = assertThrows(ConverterException.class, () -> converterChainService.performTransformation(inputFile));
        assertEquals(0, e.getConverterIndex());
    }

}
