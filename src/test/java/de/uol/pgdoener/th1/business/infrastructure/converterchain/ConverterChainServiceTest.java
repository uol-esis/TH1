package de.uol.pgdoener.th1.business.infrastructure.converterchain;

import de.uol.pgdoener.th1.business.dto.FillEmptyRowStructureDto;
import de.uol.pgdoener.th1.business.dto.RemoveRowByIndexStructureDto;
import de.uol.pgdoener.th1.business.dto.TableStructureDto;
import de.uol.pgdoener.th1.business.infrastructure.ConverterResult;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.converterchain.core.ConverterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
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
    void testPerformTransformationNoStructures() throws IOException {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(List.of("A", "B"), List.of("C", "D")), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationNoStructuresEmpty() throws IOException {
        String[][] inputMatrix = {};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationNoStructuresEmpty2() throws IOException {
        String[][] inputMatrix = {{}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        ConverterResult result = converterChainService.performTransformation(inputFile);

        assertEquals(List.of(List.of()), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationStructures() throws IOException {
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
    void testPerformTransformationIOException() throws IOException {
        when(inputFile.asStringArray()).thenThrow(new IOException("Test exception"));

        TableStructureDto tableStructure = new TableStructureDto();
        ConverterChainService converterChainService = new ConverterChainService(tableStructure);

        assertThrows(TransformationException.class, () -> converterChainService.performTransformation(inputFile));
    }

    @Test
    void testPerformTransformationException() throws IOException {
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
