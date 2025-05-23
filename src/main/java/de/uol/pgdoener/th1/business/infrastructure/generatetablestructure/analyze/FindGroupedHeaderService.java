package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze;

import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfoService;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfoService;
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
    private final CellInfoService cellInfoService;

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
        CellInfo firstCell = matrixInfo.rowInfos().getFirst().cellInfos().getFirst();
        if (!cellInfoService.hasEntry(firstCell)) {
            // no entry in cell 0,0 => no grouped header
            return Optional.empty();
        }

        // find width of rectangle
        int width = matrixInfoService.detectRectangleWidth(matrixInfo);
        // find height of rectangle
        int height = matrixInfoService.detectRectangleHeight(matrixInfo, width);

        if (!validateRectangle(matrixInfo, width, height)) {
            return Optional.empty();
        }

        log.debug("Grouped header detected with width {} and height {}", width, height);

        GroupedHeaderReportDto headerReport = buildGroupHeaderReport(width, height, matrix);

        return Optional.of(headerReport);
    }

    private GroupedHeaderReportDto buildGroupHeaderReport(int width, int height, String[][] matrix) {
        GroupedHeaderReportDto headerReport = new GroupedHeaderReportDto();

        List<Integer> rowsToFill = rangeFromZero(height - 1);
        headerReport.setRowsToFill(rowsToFill);

        List<Integer> columnsToFill = rangeFromZero(width - 1);
        headerReport.setColumnsToFill(columnsToFill);

        headerReport.setStartRow(height + 1);
        headerReport.setStartColumn(width);

        List<Integer> rowIndices = rangeFromZero(height);
        headerReport.setRowIndex(rowIndices);

        List<Integer> columnIndices = rangeFromZero(width);
        headerReport.setColumnIndex(columnIndices);

        List<String> columnHeaderNames = Arrays.asList(matrix[height]).subList(0, width);
        List<String> headerNames = new ArrayList<>(columnHeaderNames);
        for (int i = 0; i < height; i++) {
            String rowHeaderName = matrix[i][0];
            headerNames.add(rowHeaderName);
        }
        headerReport.setHeaderNames(headerNames);

        return headerReport;
    }

    private List<Integer> rangeFromZero(int endExclusive) {
        return IntStream.range(0, endExclusive)
                .boxed()
                .toList();
    }

    private boolean validateRectangle(MatrixInfo matrixInfo, int width, int height) {
        if (height > matrixInfo.rowInfos().size() / 2) {
            // unrealistic height
            return false;
        }

        // check if the rectangle is valid
        if (!matrixInfoService.isRectangleValid(matrixInfo, width, height)) {
            return false;
        }

        // check if there is a column header row
        return matrixInfoService.isValidGroupedHeaderColumnHeader(matrixInfo, width, height);
    }

}
