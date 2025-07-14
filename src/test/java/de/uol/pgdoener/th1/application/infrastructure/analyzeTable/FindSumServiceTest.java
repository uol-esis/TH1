package de.uol.pgdoener.th1.application.infrastructure.analyzeTable;

import de.uol.pgdoener.th1.application.dto.SumReportDto;
import de.uol.pgdoener.th1.application.analyzeTable.model.MatrixInfo;
import de.uol.pgdoener.th1.application.analyzeTable.factory.CellInfoFactory;
import de.uol.pgdoener.th1.application.analyzeTable.factory.MatrixInfoFactory;
import de.uol.pgdoener.th1.application.analyzeTable.finder.FindSumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MatrixInfoFactory.class,
        CellInfoFactory.class,
})
class FindSumServiceTest {

    @Autowired
    MatrixInfoFactory matrixInfoFactory;

    FindSumService findSumService = new FindSumService();

    @Test
    void testFind() {
        String[][] matrix = new String[][]{
                {"Header1", "Header2"},
                {"a", "sum"},
                {"b", "Gesamt"},
                {"c", "nix"}
        };
        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);

        Optional<SumReportDto> reports = findSumService.find(matrixInfo, matrix, List.of("sum", "gesamt"));

        assertTrue(reports.isPresent());
        assertEquals(List.of(), reports.get().getColumnIndex());
        assertEquals(List.of(1, 2), reports.get().getRowIndex());
    }

}
