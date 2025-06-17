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
        log.info("Column mismatch: {}", reports.get());
    }

}
