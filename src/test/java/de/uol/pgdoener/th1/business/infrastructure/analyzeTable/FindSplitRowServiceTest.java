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

   /* @Test
    void shouldDetectCommaDelimiter() {
        String[][] matrix = {
                {"Apple, Banana"},
                {"Orange, Pineapple"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo(",");
    }*/

   /* @Test
    void shouldDetectSemicolonDelimiter() {
        String[][] matrix = {
                {"A; B; C"},
                {"D; E; F"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo(";");
    }*/

    /*@Test
    void shouldDetectBulletDelimiter() {
        String[][] matrix = {
                {"• Entry 1 • Entry 2"},
                {"• Entry 3 • Entry 4"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo("•");
    }*/

    /*@Test
    void shouldDetectPipeDelimiter() {
        String[][] matrix = {
                {"Red | Green | Blue"},
                {"Yellow | Pink | Black"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo("\\|");
    }*/

   /* @Test
    void shouldDetectDoubleSpaceDelimiter() {
        String[][] matrix = {
                {"First  Second  Third"},
                {"Fourth  Fifth  Sixth"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo("\\s{2,}");
    }*/

    /*@Test
    void shouldDetectTabDelimiter() {
        String[][] matrix = {
                {"One\tTwo\tThree"},
                {"Four\tFive\tSix"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo("\\t");
    }*/

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

   /* @Test
    void shouldHandleMultipleColumnsAndReturnCorrectOnes() {
        String[][] matrix = {
                {"A; B", "Just one"},
                {"C; D", "Still single"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(result.get().getFirst().getDelimiter()).isEqualTo(";");
    }*/

    /*@Test
    void shouldDetectSplitColumnsInLargerMixedMatrix() {
        // Matrix mit gemischten Spalten:
        // - Spalte 0: nur normale Werte
        // - Spalte 1: mit Kommas
        // - Spalte 2: mit Newlines
        // - Spalte 3: mit Pipe (|)

        String[][] matrix = new String[][]{
                {"Name", "Fruit, Vegetable", "Line1\nLine2", "Dog | Cat"},
                {"Anna", "Apple, Carrot", "Hello\nWorld", "Bird | Fish"},
                {"Bob", "Banana, Pea", "First\nSecond", "Horse | Cow"},
                {"Carl", "Orange", "JustOneLine", "SingleAnimal"}
        };

        MatrixInfo matrixInfo = matrixInfoFactory.create(matrix);
        Optional<List<SplitRowReportDto>> result = service.find(matrixInfo, matrix);

        assertThat(result).isPresent();
        List<SplitRowReportDto> reports = result.get();

        assertThat(reports).hasSize(3); // 3 Spalten enthalten Listen

        // Column 1 should have comma as delimiter
        assertThat(reports)
                .anySatisfy(r -> {
                    assertThat(r.getColumnIndex()).isEqualTo(1);
                    assertThat(r.getDelimiter()).isEqualTo(",");
                });

        // Column 2 should have newline as delimiter
        assertThat(reports)
                .anySatisfy(r -> {
                    assertThat(r.getColumnIndex()).isEqualTo(2);
                    assertThat(r.getDelimiter()).isEqualTo("\\r?\\n");
                });

        // Column 3 should have pipe as delimiter
        assertThat(reports)
                .anySatisfy(r -> {
                    assertThat(r.getColumnIndex()).isEqualTo(3);
                    assertThat(r.getDelimiter()).isEqualTo("\\|");
                });
    }*/
}

