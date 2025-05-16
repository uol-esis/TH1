package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.business.dto.MergeableColumnsReportDto;
import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
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
            // create report if more at least one column can be merged with the current one
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
        List<Integer> headerIndex = matrixInfoService.getHeaderRows(matrixInfo);
        List<RowInfo> headerRowInfos = headerIndex.stream()
                .map(i -> matrixInfo.rowInfos().get(i))
                .toList();

        List<Integer> rowsToFill = new ArrayList<>();
        for (RowInfo rowInfo : headerRowInfos) {
            ///  TODO aufräumen !!!!
            List<CellInfo> cellInfos = rowInfo.cellInfos();
            int filledPositionsSize = rowInfoService.getFilledPositionsSize(cellInfos);

            if (filledPositionsSize <= cellInfos.size()) {
                rowsToFill.add(rowInfo.rowId());
            } else if (!rowsToFill.isEmpty()) {
                if (rowsToFill.size() == filledPositionsSize) {
                    return Optional.of(new GroupedHeaderReportDto().rowIndex(rowsToFill));
                }
            }
        }

        return Optional.empty();
    }

}
