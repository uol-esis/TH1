package de.uol.pgdoener.th1.application.analyzeTable.finder;

import de.uol.pgdoener.th1.application.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.application.analyzeTable.model.MatrixInfo;
import de.uol.pgdoener.th1.application.analyzeTable.model.MatrixInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindGroupedHeaderService {

    private final MatrixInfoService matrixInfoService;

    /**
     * This method detects the rectangle of a grouped header in the top left corner of the matrix.
     * <p>
     * A grouped header rectangle has entries in the first column.
     * To the right there are empty cells until the data starts.
     * So each row in the rectangle has exactly one filled cell in the first column.
     * Below the rectangle there are the headers for the grouped header columns.
     * To the right of those there are only empty cells.
     *
     * @param matrixInfo the matrix to analyze
     * @return the rectangle of the grouped header
     */
    public Optional<GroupedHeaderReportDto> find(MatrixInfo matrixInfo, String[][] matrix) {
        if (!matrixInfoService.hasFirstEntry(matrixInfo)) {
            return Optional.empty();
        }

        Optional<HeaderRectangle> rectangle = findHeaderRectangle(matrixInfo);
        if (rectangle.isEmpty()) {
            return Optional.empty();
        }

        if (!validateRectangle(matrixInfo, rectangle.get())) {
            return Optional.empty();
        }

        log.debug("Grouped header detected with {}", rectangle);

        GroupedHeaderReportDto headerReport = buildGroupHeaderReport(rectangle.get(), matrix);

        return Optional.of(headerReport);
    }

    private Optional<HeaderRectangle> findHeaderRectangle(MatrixInfo matrixInfo) {
        int width = matrixInfoService.detectRectangleWidth(matrixInfo);
        if (width >= matrixInfo.columnInfos().size()) {
            return Optional.empty();
        }

        int height = matrixInfoService.detectRectangleHeight(matrixInfo, width);
        if (height >= matrixInfo.rowInfos().size()) {
            return Optional.empty();
        }

        return Optional.of(new HeaderRectangle(width, height));
    }

    private GroupedHeaderReportDto buildGroupHeaderReport(HeaderRectangle rectangle, String[][] matrix) {
        int width = rectangle.width();
        int height = rectangle.height();

        GroupedHeaderReportDto report = new GroupedHeaderReportDto();
        report.setRowsToFill(rangeFromZero(height - 1));
        report.setColumnsToFill(rangeFromZero(width - 1));
        report.setStartRow(height + 1);
        report.setStartColumn(width);
        report.setRowIndex(rangeFromZero(height));
        report.setColumnIndex(rangeFromZero(width));
        report.setHeaderNames(extractHeaderNames(matrix, width, height));

        return report;
    }

    private List<String> extractHeaderNames(String[][] matrix, int width, int height) {
        List<String> columnHeaderNames = Arrays.asList(matrix[height]).subList(0, width);
        List<String> headerNames = new ArrayList<>(columnHeaderNames);
        for (int i = 0; i < height; i++) {
            String rowHeaderName = matrix[i][0];
            headerNames.add(rowHeaderName);
        }
        headerNames.add("Wert");
        return headerNames;
    }

    private List<Integer> rangeFromZero(int endExclusive) {
        return IntStream.range(0, endExclusive)
                .boxed()
                .toList();
    }

    private boolean validateRectangle(MatrixInfo matrixInfo, HeaderRectangle rectangle) {
        if (rectangle.height() > matrixInfo.rowInfos().size() / 2) {
            // unrealistic height
            return false;
        }

        // check if the rectangle is valid
        if (!matrixInfoService.isRectangleValid(matrixInfo, rectangle.width(), rectangle.height())) {
            return false;
        }

        // check if there is a column header row
        return matrixInfoService.isValidGroupedHeaderColumnHeader(matrixInfo, rectangle.width(), rectangle.height());
    }

    /**
     * Internal record for clarity when handling both dimensions together.
     */
    private record HeaderRectangle(
            int width,
            int height
    ) {
    }

}
