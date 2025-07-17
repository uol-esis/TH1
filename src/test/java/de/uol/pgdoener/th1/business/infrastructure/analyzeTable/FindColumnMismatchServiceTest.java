package de.uol.pgdoener.th1.business.infrastructure.analyzeTable;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.*;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.MatrixInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder.FindColumnMismatchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        FindColumnMismatchService.class,
        MatrixInfoFactory.class,
        CellInfoFactory.class,
        MatrixInfoService.class,
        ColumnInfoService.class,
        RowInfoService.class,
        CellInfoService.class
})
class FindColumnMismatchServiceTest {

    @Autowired
    MatrixInfoFactory matrixInfoFactory;

    @Autowired
    FindColumnMismatchService findColumnMismatchService;

    @Test
    void testFindColumnMismatch() {
        String[][] matrix = new String[][]{
                {"Header1", "Header2"},
                {"1", "9"},
                {"-", "2"},
                {"4", ""},
                {"5", "3"},
                {"6", "a"},
                {"7", "8"},
                {"8", "-"},
                {"9", "10"},
                {"10", "11"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);

        Optional<ColumnTypeMismatchReportDto> reports = findColumnMismatchService.find(matrixInfo, matrix);

        assertTrue(reports.isPresent());
        assertEquals(reports.get().getMismatches().getFirst().getReplacementSearch(), Optional.of(""));
        assertEquals(reports.get().getMismatches().getFirst().getReplacementValue(), Optional.of("*"));
        assertEquals(reports.get().getMismatches().get(1).getReplacementSearch(), Optional.of("a"));
        assertEquals(reports.get().getMismatches().get(1).getReplacementValue(), Optional.empty());
        assertEquals(reports.get().getMismatches().get(2).getReplacementSearch(), Optional.of("-"));
        assertEquals(reports.get().getMismatches().get(2).getReplacementValue(), Optional.of("*"));
    }

    @Test
    void testFindColumnMismatchWithGroupedHeader() {
        String[][] matrix = new String[][]{
                {"Header1", "Header2"},
                {"Header3", "Header4"},
                {"1", "9"},
                {"-", "2"},
                {"4", ""},
                {"5", "3"},
                {"6", "a"},
                {"7", "8"},
                {"8", "-"},
                {"9", "10"},
                {"10", "11"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);

        Optional<ColumnTypeMismatchReportDto> reports = findColumnMismatchService.find(matrixInfo, matrix);

        assertTrue(reports.isPresent());
        assertEquals(reports.get().getMismatches().getFirst().getReplacementSearch(), Optional.of(""));
        assertEquals(reports.get().getMismatches().getFirst().getReplacementValue(), Optional.of("*"));
        assertEquals(reports.get().getMismatches().get(1).getReplacementSearch(), Optional.of("a"));
        assertEquals(reports.get().getMismatches().get(1).getReplacementValue(), Optional.empty());
        assertEquals(reports.get().getMismatches().get(2).getReplacementSearch(), Optional.of("Header3"));
        assertEquals(reports.get().getMismatches().get(2).getReplacementValue(), Optional.empty());
        assertEquals(reports.get().getMismatches().get(3).getReplacementSearch(), Optional.of("Header4"));
        assertEquals(reports.get().getMismatches().get(3).getReplacementValue(), Optional.empty());
        assertEquals(reports.get().getMismatches().get(4).getReplacementSearch(), Optional.of("-"));
        assertEquals(reports.get().getMismatches().get(4).getReplacementValue(), Optional.of("*"));
    }

}
