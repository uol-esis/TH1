package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.business.dto.MergeableColumnsReportDto;
import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeMatrixInfoService {

    private final MatrixInfoService matrixInfoService;
    private final RowInfoService rowInfoService;
    private final ColumnInfoService columnInfoService;
    private final CellInfoService cellInfoService;

    public List<ReportDto> analyze(MatrixInfo matrixInfo, String[][] matrix) {
        List<ReportDto> reports = new ArrayList<>();

        Optional<GroupedHeaderReportDto> optionalGroupedHeaderReport = findGroupedHeader(matrixInfo, matrix);
        if (optionalGroupedHeaderReport.isPresent()) {
            reports.add(optionalGroupedHeaderReport.get());
            return reports;
        }
        findColumnTypeMismatches(matrixInfo).ifPresent(reports::add);
        findMergeableColumns(matrixInfo).ifPresent(reports::addAll);

        return reports;
    }

    private Optional<ColumnTypeMismatchReportDto> findColumnTypeMismatches(MatrixInfo matrixInfo) {
        List<Integer> mismatches = matrixInfo.columnInfos().stream()
                .filter(columnInfoService::hasTypeMismatch)
                .map(ColumnInfo::columnIndex)
                .toList();
        if (mismatches.isEmpty()) return Optional.empty();
        return Optional.of(new ColumnTypeMismatchReportDto().columnIndex(mismatches));
    }

    private Optional<List<MergeableColumnsReportDto>> findMergeableColumns(MatrixInfo matrixInfo) {
        List<ColumnInfo> columnInfos = matrixInfo.columnInfos();
        List<MergeableColumnsReportDto> reports = new ArrayList<>();
        List<ColumnInfo> included = new ArrayList<>();

        for (int i = 0; i < columnInfos.size(); i++) {
            List<ColumnInfo> columnsToCheck = new ArrayList<>();
            columnsToCheck.add(columnInfos.get(i));

            // look for partners to the right
            for (int j = i + 1; j < columnInfos.size(); j++) {
                // skip if it is already included in another merge report
                if (included.contains(columnInfos.get(j))) continue;

                // test if it can be merged with the current set
                columnsToCheck.addLast(columnInfos.get(j));
                if (columnInfoService.areMergeable(columnsToCheck)) {
                    included.add(columnInfos.get(j));
                } else {
                    columnsToCheck.removeLast();
                }
            }
            // create report if at least one column can be merged with the current one
            if (columnsToCheck.size() > 1) {
                reports.add(new MergeableColumnsReportDto()
                        .mergeables(columnsToCheck.stream()
                                .map(ColumnInfo::columnIndex)
                                .toList()
                        )
                );
            }
        }

        if (reports.isEmpty()) return Optional.empty();
        return Optional.of(reports);
    }

    private Optional<GroupedHeaderReportDto> findGroupedHeader(MatrixInfo matrixInfo, String[][] matrix) {
        Optional<Pair<Integer, Integer>> rectangle = matrixInfoService.detectGroupedHeaderCorner(matrixInfo);
        if (rectangle.isEmpty()) {
            log.debug("No grouped header detected");
            return Optional.empty();
        }
        int width = rectangle.get().getFirst();
        int height = rectangle.get().getSecond();

        log.debug("Grouped header detected with width {} and height {}", width, height);

        GroupedHeaderReportDto headerReport = new GroupedHeaderReportDto();
        List<Integer> rowsToFill = IntStream.range(0, height - 1)
                .boxed()
                .toList();
        headerReport.setRowsToFill(rowsToFill);
        List<Integer> columnsToFill = IntStream.range(0, width - 1)
                .boxed()
                .toList();
        headerReport.setColumnsToFill(columnsToFill);

        headerReport.setStartRow(height + 1);
        headerReport.setStartColumn(width);

        List<Integer> rowIndices = IntStream.range(0, height)
                .boxed()
                .toList();
        headerReport.setRowIndex(rowIndices);
        List<Integer> columnIndices = IntStream.range(0, width)
                .boxed()
                .toList();
        headerReport.setColumnIndex(columnIndices);

        List<String> columnHeaderNames = Arrays.asList(matrix[height]).subList(0, width);
        List<String> headerNames = new ArrayList<>(columnHeaderNames);
        for (int i = 0; i < height; i++) {
            String rowHeaderName = matrix[i][0];
            headerNames.add(rowHeaderName);
        }
        headerReport.setHeaderNames(headerNames);

        List<String> headerNames = rowInfoService.getHeaderNames(headerRows, headerColumns);
        System.out.println(headerNames);

        return Optional.of(headerReport);
    }

}
