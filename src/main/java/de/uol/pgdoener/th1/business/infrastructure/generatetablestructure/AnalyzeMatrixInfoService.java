package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.business.dto.MergeableColumnsReportDto;
import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.RowInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeMatrixInfoService {

    private final MatrixInfoService matrixInfoService;
    private final RowInfoService rowInfoService;
    private final ColumnInfoService columnInfoService;
    private final CellInfoService cellInfoService;

    public List<ReportDto> analyze(MatrixInfo matrixInfo) {
        List<ReportDto> reports = new ArrayList<>();

        findColumnTypeMismatches(matrixInfo).ifPresent(reports::add);
        findMergeableColumns(matrixInfo).ifPresent(reports::addAll);
        findGroupedHeader(matrixInfo).ifPresent(reports::add);

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

    private Optional<GroupedHeaderReportDto> findGroupedHeader(MatrixInfo matrixInfo) {
        List<RowInfo> headerRows = matrixInfoService.getHeaderRowsInfo(matrixInfo);
        List<ColumnInfo> headerColumns = matrixInfoService.getHeaderColumns(matrixInfo);

        if (headerRows.size() < 2 && headerColumns.size() < 2) {
            log.debug("No GroupedHeaderReportDto found");
            return Optional.empty();
        }
        GroupedHeaderReportDto headerReport = new GroupedHeaderReportDto();
        List<RowInfo> incompleteHeaderRows = rowInfoService.getRowsToFill(headerRows);
        List<ColumnInfo> incompleteHeaderColumns = rowInfoService.getColumnsToFill(headerColumns);

        if (!incompleteHeaderRows.isEmpty()) {
            // add to Report
            log.debug("Unvollständige Headerzeilen erkannt: {}", incompleteHeaderRows);
        }
        if (!incompleteHeaderColumns.isEmpty()) {
            // add to report
            log.debug("Unvollständige Headerspalten erkannt: {}", incompleteHeaderColumns);
        }

        if (!headerRows.isEmpty()) {
            fillRowSection(matrixInfo, headerReport, headerRows);
        }
        if (!headerColumns.isEmpty()) {
            fillColumnSection(matrixInfo, headerReport, headerColumns);
        }

        return Optional.of(headerReport);
    }

    private void fillRowSection(MatrixInfo matrixInfo, GroupedHeaderReportDto report, List<RowInfo> headerRows) {
        List<Integer> rowIndices = headerRows.stream().map(RowInfo::rowId).toList();
        int lastHeaderRowIndex = rowIndices.getLast();
        int dataStartRowIndex = matrixInfoService.getFirstDataRowIndex(matrixInfo, lastHeaderRowIndex + 1);

        report.setRowIndex(rowIndices);
        report.setStartRow(dataStartRowIndex);
    }

    private void fillColumnSection(MatrixInfo matrixInfo, GroupedHeaderReportDto report, List<ColumnInfo> headerColumns) {
        List<Integer> columnIndices = headerColumns.stream().map(ColumnInfo::columnIndex).toList();
        int lastHeaderColumnIndex = columnIndices.getLast();
        int dataStartColumnIndex = matrixInfoService.getFirstDataColumnIndex(matrixInfo, lastHeaderColumnIndex + 1);

        report.setColumnIndex(columnIndices);
        report.setStartColumn(dataStartColumnIndex);
    }
}
