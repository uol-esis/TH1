package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.*;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.MatrixInfoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateTableStructureServiceTest {

    CellInfoService cellInfoService = new CellInfoService();
    ColumnInfoService columnInfoService = new ColumnInfoService(cellInfoService);
    RowInfoService rowInfoService = new RowInfoService(cellInfoService);
    MatrixInfoService matrixInfoService = new MatrixInfoService(rowInfoService);

    CellInfoFactory cellInfoFactory = new CellInfoFactory();
    MatrixInfoFactory matrixInfoFactory = new MatrixInfoFactory(cellInfoFactory);

    AnalyzeMatrixInfoService analyzeMatrixInfoService = new AnalyzeMatrixInfoService(matrixInfoService, rowInfoService, columnInfoService, cellInfoService);

    GenerateTableStructureService service = new GenerateTableStructureService(matrixInfoService, matrixInfoFactory, analyzeMatrixInfoService);

    @Test
    void testGenerationGroupedHeader() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderTwoHeader.csv"));
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

        TableStructureDto tableStructure = result.getFirst();
        List<ReportDto> unresolvedReports = result.getSecond();
        System.out.println(result.getFirst());
        System.out.println(result.getSecond());

        assertInstanceOf(RemoveHeaderStructureDto.class, tableStructure.getStructures().getFirst());
        assertInstanceOf(RemoveFooterStructureDto.class, tableStructure.getStructures().get(1));
        assertInstanceOf(RemoveTrailingColumnStructureDto.class, tableStructure.getStructures().get(2));
        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(3));
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(4));
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(5));

        assertTrue(unresolvedReports.isEmpty());
    }

    InputStream getInputStream(String path) {
        return getClass().getResourceAsStream(path);
    }

}
