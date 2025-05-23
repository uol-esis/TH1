package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze;

import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
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
public class FindGroupedHeaderServiceNew {

    private static final String COUNT_COLUMN_LABEL = "Anzahl";

    private final MatrixInfoService matrixInfoService;

    /**
     * Tries to detect a grouped header area in the top-left corner of the matrix.
     * <p>
     * A grouped header structure usually includes:
     * - Group names listed vertically in the first column.
     * - Each row of this area has only the first column filled; the rest are empty.
     * - A horizontal header row appears directly beneath this block.
     *
     * @param matrixInfo structural metadata of the matrix
     * @param matrix     the actual matrix (2D String array)
     * @return Optional containing the grouped header report, or empty if not applicable
     */
    public Optional<GroupedHeaderReportDto> find(MatrixInfo matrixInfo, String[][] matrix) {
        if (!matrixInfoService.hasFirstEntry(matrixInfo)) {
            return Optional.empty();
        }

        HeaderDimensions dimensions = findHeaderDimensions(matrixInfo);
        if (dimensions.isEmpty()) {
            return Optional.empty();
        }

        log.debug("Grouped header found: {} rows x {} columns", dimensions.rowCount(), dimensions.columnCount());

        GroupedHeaderReportDto report = createGroupedHeaderReport(dimensions, matrix);
        return Optional.of(report);
    }

    private HeaderDimensions findHeaderDimensions(MatrixInfo matrixInfo) {
        int columnCount = matrixInfoService.detectColumnHeaderEndIndex(matrixInfo);
        int rowCount = matrixInfoService.detectRowHeaderEndIndex(matrixInfo, columnCount);
        return new HeaderDimensions(rowCount, columnCount);
    }

    /**
     * Creates the DTO describing the detected grouped header structure.
     */
    private GroupedHeaderReportDto createGroupedHeaderReport(HeaderDimensions dimensions, String[][] matrix) {
        int rowCount = dimensions.rowCount();
        int columnCount = dimensions.columnCount();

        GroupedHeaderReportDto report = new GroupedHeaderReportDto();
        report.setRowsToFill(rangeFromZero(rowCount - 1));
        report.setColumnsToFill(rangeFromZero(columnCount - 1));
        report.setStartRow(rowCount + 1);
        report.setStartColumn(columnCount + 1);
        report.setRowIndex(rangeFromZero(rowCount));
        report.setColumnIndex(rangeFromZero(columnCount + 1));
        report.setHeaderNames(extractHeaderNames(matrix, dimensions));

        return report;
    }

    private List<String> extractHeaderNames(String[][] matrix, HeaderDimensions dimensions) {
        int headerRowIndex = dimensions.rowCount();
        int columnCount = dimensions.columnCount();

        // Horizontal headers (under the grouped block)
        List<String> columnHeader = Arrays.asList(matrix[headerRowIndex]).subList(0, columnCount + 1);
        List<String> headers = new ArrayList<>(columnHeader);

        // Vertical group headers (first column)
        for (int i = 0; i < dimensions.rowCount(); i++) {
            String rowHeaderName = matrix[i][0];
            headers.add(rowHeaderName);
        }

        headers.add(COUNT_COLUMN_LABEL);
        return headers;
    }

    private List<Integer> rangeFromZero(int endExclusive) {
        return IntStream.range(0, endExclusive)
                .boxed()
                .toList();
    }

    /**
     * Internal record for clarity when handling both dimensions together.
     */
    private record HeaderDimensions(int rowCount, int columnCount) {
        boolean isEmpty() {
            return rowCount == 0 && columnCount == 0;
        }
    }
}
