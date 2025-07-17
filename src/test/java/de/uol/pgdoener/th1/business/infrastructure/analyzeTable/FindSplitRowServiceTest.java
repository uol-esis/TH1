package de.uol.pgdoener.th1.business.infrastructure.analyzeTable;

import de.uol.pgdoener.th1.business.dto.SplitRowReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.CellInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.MatrixInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder.FindSplitRowService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MatrixInfoFactory.class,
        CellInfoFactory.class,
})
public class FindSplitRowServiceTest {

    @Autowired
    MatrixInfoFactory matrixInfoFactory;

    private final FindSplitRowService service = new FindSplitRowService();

    @Test
    void shouldDetectNewlineDelimiter() {
        String[][] matrix = {
                {"Line 1\nLine 2"},
                {"Line 3\nLine 4"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo("\\r?\\n");
    }

    @Test
    void shouldReturnEmptyIfOnlyOneItemPerCell() {
        String[][] matrix = {
                {"Single"},
                {"Value"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyIfCellContentIsNullOrBlank() {
        String[][] matrix = {
                {" "},
                {"   "}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOnlyFirstValidDelimiterPerColumn() {
        String[][] matrix = {
                {"A; B\nC"},     // multiple getDelimiters
                {"D; E\nF"}      // multiple getDelimiters
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        // Only one getDelimiter is returned per column
        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isIn(";", "\\r?\\n"); // depending on implementation order
    }
}

