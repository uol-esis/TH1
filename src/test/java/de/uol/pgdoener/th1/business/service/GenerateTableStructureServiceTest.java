package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.InputFile;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.AnalyzeMatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.CellInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.RowInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.MatrixInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder.*;
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
        FindSplitRowService.class,
        FindSumService.class,
        FindColumnMismatchService.class,
        FindMergableColumnsService.class,
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

        assertInstanceOf(RemoveHeaderStructureDto.class, tableStructure.getStructures().getFirst());
        assertInstanceOf(RemoveFooterStructureDto.class, tableStructure.getStructures().get(1));
        assertInstanceOf(RemoveTrailingColumnStructureDto.class, tableStructure.getStructures().get(2));
        assertInstanceOf(RemoveLeadingColumnStructureDto.class, tableStructure.getStructures().get(3));
        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(4));
        assertEquals(List.of(0), ((FillEmptyRowStructureDto) tableStructure.getStructures().get(4)).getRowIndex());
        assertInstanceOf(FillEmptyColumnStructureDto.class, tableStructure.getStructures().get(5));
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(6));
        assertEquals(List.of(0, 1), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getRowIndex());
        assertEquals(List.of(0, 1, 2), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getColumnIndex());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getStartColumn().orElseThrow());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(7));
        assertEquals(List.of("Sozialräume", "Stadtteile", "Stadtviertel", "Geschlecht", "Altersgruppen", "Wert"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(7)).getHeaderNames());
        assertInstanceOf(RemoveKeywordsStructureDto.class, tableStructure.getStructures().get(8));
        assertInstanceOf(ReplaceEntriesStructureDto.class, tableStructure.getStructures().get(9));
        assertEquals("", ((ReplaceEntriesStructureDto) tableStructure.getStructures().get(9)).getSearch().orElseThrow());
        assertEquals("*", ((ReplaceEntriesStructureDto) tableStructure.getStructures().get(9)).getReplacement());
        assertInstanceOf(ReplaceEntriesStructureDto.class, tableStructure.getStructures().get(10));
        assertEquals("-", ((ReplaceEntriesStructureDto) tableStructure.getStructures().get(10)).getSearch().orElseThrow());
        assertEquals("*", ((ReplaceEntriesStructureDto) tableStructure.getStructures().get(10)).getReplacement());
    }

    @Test
    void testGenerationGroupedHeaderI() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderI.csv"));
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

        TableStructureDto tableStructure = result.getFirst();
        List<ReportDto> unresolvedReports = result.getSecond();

        System.out.println(tableStructure.getStructures());

        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(4));
        assertInstanceOf(FillEmptyColumnStructureDto.class, tableStructure.getStructures().get(5));
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(6));
        assertEquals(List.of(0, 1), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getRowIndex());
        assertEquals(List.of(0, 1, 2), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getColumnIndex());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getStartColumn().orElseThrow());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(7));
        assertEquals(List.of("Sozialräume", "Stadtteile", "Stadtviertel", "Geschlecht", "Altersgruppen", "Wert"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(7)).getHeaderNames());
    }


//    @Test
//    void testGenerationGroupedHeaderI_I() throws IOException {
//        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderI-I.csv"));
//        InputFile inputFile = new InputFile(file);
//        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();
//
//        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);
//
//        TableStructureDto tableStructure = result.getFirst();
//        List<ReportDto> unresolvedReports = result.getSecond();
//
//        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(4));
//        assertInstanceOf(FillEmptyColumnStructureDto.class, tableStructure.getStructures().get(5));
//        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(6));
//        assertEquals(List.of(0, 1), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getRowIndex());
//        assertEquals(List.of(0, 1, 2), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getColumnIndex());
//        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getStartColumn().orElseThrow());
//        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(6)).getStartRow().orElseThrow());
//        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(7));
//        assertEquals(List.of("Sozialräume", "Stadtteile", "Stadtviertel", "Geschlecht", "Altersgruppen"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(7)).getHeaderNames());
//    }

    @Test
    void testGenerationGroupedHeaderII() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderII.csv"));
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

        TableStructureDto tableStructure = result.getFirst();
        List<ReportDto> unresolvedReports = result.getSecond();

        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(5));
        assertEquals(List.of(0), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getRowIndex());
        assertEquals(List.of(0, 1, 2), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getColumnIndex());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getStartColumn().orElseThrow());
        assertEquals(2, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(6));
        assertEquals(List.of("Sozialräume", "Stadtteile", "Stadtviertel", "Altersgruppen", "Wert"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(6)).getHeaderNames());
    }

//    @Test
//    void testGenerationGroupedHeaderIII() throws IOException {
//        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", getInputStream("/unit/groupedHeaderIII.csv"));
//        InputFile inputFile = new InputFile(file);
//        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();
//
//        Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);
//
//        TableStructureDto tableStructure = result.getFirst();
//        List<ReportDto> unresolvedReports = result.getSecond();
//        System.out.println(tableStructure);
//        System.out.println(unresolvedReports);
//
//        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(5));
//        assertEquals(List.of(0), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getRowIndex());
//        assertEquals(List.of(0, 1, 2), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getColumnIndex());
//        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getStartColumn().orElseThrow());
//        assertEquals(2, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getStartRow().orElseThrow());
//        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(6));
//        assertEquals(List.of("Sozialräume", "Stadtteile", "Stadtviertel", "Altersgruppen"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(6)).getHeaderNames());
//    }

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
        assertInstanceOf(RemoveLeadingColumnStructureDto.class, tableStructure.getStructures().get(3));
        assertInstanceOf(FillEmptyRowStructureDto.class, tableStructure.getStructures().get(4));
        assertEquals(List.of(0), ((FillEmptyRowStructureDto) tableStructure.getStructures().get(4)).getRowIndex());
        assertInstanceOf(RemoveGroupedHeaderStructureDto.class, tableStructure.getStructures().get(5));
        assertEquals(List.of(0, 1), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getRowIndex());
        assertEquals(List.of(0), ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getColumnIndex());
        assertEquals(1, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getStartColumn().orElseThrow());
        assertEquals(3, ((RemoveGroupedHeaderStructureDto) tableStructure.getStructures().get(5)).getStartRow().orElseThrow());
        assertInstanceOf(AddHeaderNameStructureDto.class, tableStructure.getStructures().get(6));
        assertEquals(List.of("Stadtviertel", "Geschlecht", "Altersgruppen", "Wert"), ((AddHeaderNameStructureDto) tableStructure.getStructures().get(6)).getHeaderNames());
        assertInstanceOf(RemoveKeywordsStructureDto.class, tableStructure.getStructures().get(7));
        assertInstanceOf(ReplaceEntriesStructureDto.class, tableStructure.getStructures().get(8));
        assertEquals("-", ((ReplaceEntriesStructureDto) tableStructure.getStructures().get(8)).getSearch().orElseThrow());
        assertEquals("*", ((ReplaceEntriesStructureDto) tableStructure.getStructures().get(8)).getReplacement());
    }

    @Test
    void testLargeFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "", generateLargeCSV().getBytes());
        InputFile inputFile = new InputFile(file);
        TableStructureGenerationSettingsDto settings = new TableStructureGenerationSettingsDto();

        for (int i = 0; i < 1; i++) {
            Pair<TableStructureDto, List<ReportDto>> result = service.generateTableStructure(inputFile, settings);

            TableStructureDto tableStructure = result.getFirst();
            List<ReportDto> unresolvedReports = result.getSecond();
            System.out.println(tableStructure);
            System.out.println(unresolvedReports);

            assertInstanceOf(RemoveHeaderStructureDto.class, tableStructure.getStructures().getFirst());
        }

    }

    private String generateLargeCSV() {
        final int lines = 5000;
        final int columns = 200;

        StringBuilder builder = new StringBuilder();
        builder.append("invalid row;;;;;;;\n");
        builder.append(";;;;;;;;;;\n");
        builder.append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n");
        builder.append("invalid row;;;;;;;\n");
        // header line
        for (int i = 0; i < columns; i++) {
            builder.append("header").append(i).append(";");
        }
        builder.append("\n");

        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                builder.append("entry").append(i * j).append(";");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    InputStream getInputStream(String path) {
        return getClass().getResourceAsStream(path);
    }

}
