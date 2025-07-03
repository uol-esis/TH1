package de.uol.pgdoener.th1.business.infrastructure.analyzeTable;

import de.uol.pgdoener.th1.business.dto.TransposeMatrixReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.*;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.MatrixInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder.FindTransposedMatrixService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        FindTransposedMatrixService.class,
        MatrixInfoFactory.class,
        CellInfoFactory.class,
        MatrixInfoService.class,
        ColumnInfoService.class,
        RowInfoService.class,
        CellInfoService.class,
        MatrixInfo.class
})
class FindTransposedMatrixServiceTest {

    @Autowired
    MatrixInfoFactory matrixInfoFactory;

    @Autowired
    FindTransposedMatrixService findTransposedMatrixService;

    @Test
    void testFindTransposedMatrix_detectsTransposedMatrix() {
        String[][] matrix = new String[][]{
                {"Label", "2020", "2025", "2030", "2035"},
                {"Population", "11.1", "11.2", "11.3", "11.4"},
                {"Under 50", "6.3", "6.4", "6.5", "6.6"},
                {"50–60", "1.7", "1.6", "1.5", "1.4"},
                {"60–70", "1.4", "1.5", "1.6", "1.7"},
                {"Over 90", "0.1", "0.2", "0.3", "0.4"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<TransposeMatrixReportDto> result = findTransposedMatrixService.find(matrixInfo, matrix);

        assertTrue(result.isPresent());
        assertTrue(result.get().isDetected());
    }

    @Test
    void testFindTransposedMatrix_doesNotDetectNonTransposedMatrix() {
        String[][] matrix = new String[][]{
                {"Year", "Population", "Under 50", "Over 90"},
                {"2020", "11.1", "6.3", "0.1"},
                {"2025", "11.2", "6.4", "0.2"},
                {"2030", "11.3", "6.5", "0.3"},
                {"2035", "11.4", "6.6", "0.4"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<TransposeMatrixReportDto> result = findTransposedMatrixService.find(matrixInfo, matrix);

        assertFalse(result.isPresent());
    }
}
