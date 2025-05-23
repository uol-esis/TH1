package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.AnalyzeMatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.*;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory.MatrixInfoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CellInfoService.class,
        ColumnInfoService.class,
        RowInfoService.class,
        MatrixInfoService.class,
        CellInfoFactory.class,
        MatrixInfoFactory.class,
        FindGroupedHeaderService.class,
        FindEmptyRowService.class,
        FindEmptyColumnService.class,
        FindEmptyHeaderService.class,
        FindSameAsHeaderService.class,
        AnalyzeMatrixInfoService.class,
        GenerateTableStructureService.class,
})
class GenerateTableStructureServiceTest {

    @Autowired
    GenerateTableStructureService service;

    @Test
    void testGenerationGroupedHeader() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeader.csv"));
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

        TableStructureDto tableStructure = result.getFirst();
        List<ReportDto> unresolvedReports = result.getSecond();
        System.out.println(tableStructure);
        System.out.println(unresolvedReports);

        assertInstanceOf(RemoveHeaderStructureDto.class, tableStructure.getStructures().getFirst());
        assertInstanceOf(RemoveFooterStructureDto.class, tableStructure.getStructures().get(1));
        assertInstanceOf(RemoveTrailingColumnStructureDto.class, tableStructure.getStructures().get(2));
        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(3));
        assertEquals(List.of(0), ((FillEmptyRowStructureDto) tableStructure.getStructures().get(3)).getRowIndex());
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(4));
        assertEquals(List.of(0, 1), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getRowIndex());
        assertEquals(List.of(0, 1, 2), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getColumnIndex());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getStartColumn().orElseThrow());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(5));
        assertEquals(List.of("Sozialräume", "Stadtteile", "Stadtviertel", "Geschlecht", "Altersgruppen"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(5)).getHeaderNames());
    }

    @Test
    void testGenerationGroupedHeaderOneHeader() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderOneHeader.csv"));
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

        TableStructureDto tableStructure = result.getFirst();
        List<ReportDto> unresolvedReports = result.getSecond();
        System.out.println(tableStructure);
        System.out.println(unresolvedReports);

        assertInstanceOf(RemoveHeaderStructureDto.class, tableStructure.getStructures().getFirst());
        assertInstanceOf(RemoveFooterStructureDto.class, tableStructure.getStructures().get(1));
        assertInstanceOf(RemoveTrailingColumnStructureDto.class, tableStructure.getStructures().get(2));
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(3));
        assertEquals(List.of(0), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(3)).getRowIndex());
        assertEquals(List.of(0), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(3)).getColumnIndex());
        assertEquals(1, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(3)).getStartColumn().orElseThrow());
        assertEquals(2, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(3)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(4));
        assertEquals(List.of("Stadtviertel", "Altersgruppen"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(4)).getHeaderNames());
    }

    @Test
    void testGenerationGroupedHeaderTwoHeader() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderTwoHeader.csv"));
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

        TableStructureDto tableStructure = result.getFirst();
        List<ReportDto> unresolvedReports = result.getSecond();
        System.out.println(tableStructure);
        System.out.println(unresolvedReports);

        assertInstanceOf(RemoveHeaderStructureDto.class, tableStructure.getStructures().getFirst());
        assertInstanceOf(RemoveFooterStructureDto.class, tableStructure.getStructures().get(1));
        assertInstanceOf(RemoveTrailingColumnStructureDto.class, tableStructure.getStructures().get(2));
        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(3));
        assertEquals(List.of(0), ((FillEmptyRowStructureDto) tableStructure.getStructures().get(3)).getRowIndex());
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(4));
        assertEquals(List.of(0, 1), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getRowIndex());
        assertEquals(List.of(0), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getColumnIndex());
        assertEquals(1, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getStartColumn().orElseThrow());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(4)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(5));
        assertEquals(List.of("Stadtviertel", "Geschlecht", "Altersgruppen"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(5)).getHeaderNames());
    }

    InputStream getInputStream(String path) {
        return getClass().getResourceAsStream(path);
    }

}
