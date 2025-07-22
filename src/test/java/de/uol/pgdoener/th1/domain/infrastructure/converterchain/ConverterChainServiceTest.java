package de.uol.pgdoener.th1.domain.infrastructure.converterchain;

import de.uol.pgdoener.th1.application.dto.FillEmptyRowStructureDto;
import de.uol.pgdoener.th1.application.dto.RemoveRowByIndexStructureDto;
import de.uol.pgdoener.th1.application.dto.TableStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.exception.ConverterException;
import de.uol.pgdoener.th1.domain.converterchain.factory.ConverterFactory;
import de.uol.pgdoener.th1.domain.converterchain.model.ConverterChain;
import de.uol.pgdoener.th1.domain.converterchain.service.ConverterChainCreationService;
import de.uol.pgdoener.th1.domain.converterchain.service.ConverterChainService;
import de.uol.pgdoener.th1.domain.shared.exceptions.InputFileException;
import de.uol.pgdoener.th1.domain.shared.model.ConverterResult;
import de.uol.pgdoener.th1.domain.shared.model.InputFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConverterChainServiceTest {

    @Mock
    InputFile inputFile;

    ConverterFactory converterFactory = new ConverterFactory();
    ConverterChainCreationService chainFactory = new ConverterChainCreationService(converterFactory);
    ConverterChainService converterChainService = new ConverterChainService();

    @Test
    void testPerformTransformationNoStructures() {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();

        ConverterChain converterChain = chainFactory.create(tableStructure);
        String[][] outputMatrix = converterChainService.performTransformation(inputFile, converterChain);
        ConverterResult result = new ConverterResult(tableStructure, outputMatrix);

        assertEquals(List.of(List.of("A", "B"), List.of("C", "D")), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationNoStructuresEmpty() {
        String[][] inputMatrix = {};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();

        ConverterChain converterChain = chainFactory.create(tableStructure);
        String[][] outputMatrix = converterChainService.performTransformation(inputFile, converterChain);
        ConverterResult result = new ConverterResult(tableStructure, outputMatrix);

        assertEquals(List.of(), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationNoStructuresEmpty2() {
        String[][] inputMatrix = {{}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();

        ConverterChain converterChain = chainFactory.create(tableStructure);
        String[][] outputMatrix = converterChainService.performTransformation(inputFile, converterChain);
        ConverterResult result = new ConverterResult(tableStructure, outputMatrix);

        assertEquals(List.of(List.of()), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationStructures() {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        tableStructure.addStructuresItem(new RemoveRowByIndexStructureDto().addRowIndexItem(0));

        ConverterChain converterChain = chainFactory.create(tableStructure);
        String[][] outputMatrix = converterChainService.performTransformation(inputFile, converterChain);
        ConverterResult result = new ConverterResult(tableStructure, outputMatrix);

        assertEquals(List.of(List.of("C", "D")), result.dataAsListOfLists());
        assertEquals(tableStructure, result.tableStructure());
    }

    @Test
    void testPerformTransformationIOException() {
        when(inputFile.asStringArray()).thenThrow(new InputFileException("Test exception"));

        TableStructureDto tableStructure = new TableStructureDto();

        ConverterChain converterChain = chainFactory.create(tableStructure);

        assertThrows(InputFileException.class, () -> converterChainService.performTransformation(inputFile, converterChain));
    }

    @Test
    void testPerformTransformationException() {
        String[][] inputMatrix = {{"A", "B"}, {"C", "D"}};
        when(inputFile.asStringArray()).thenReturn(inputMatrix);

        TableStructureDto tableStructure = new TableStructureDto();
        // -1 is usually not possible at this point, but this is used to test the exception handling
        tableStructure.addStructuresItem(new FillEmptyRowStructureDto().addRowIndexItem(-1));

        ConverterChain converterChain = chainFactory.create(tableStructure);

        ConverterException e = assertThrows(ConverterException.class, () -> converterChainService.performTransformation(inputFile, converterChain));
        assertEquals(0, e.getConverterIndex());
    }

}
