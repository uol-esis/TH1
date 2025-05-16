package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.business.dto.MergeableColumnsReportDto;
import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
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
    private final CellInfoService cellInfoService;

    public List<ReportDto> analyze(MatrixInfo matrixInfo) {
        List<ReportDto> reports = new ArrayList<>();

        findColumnTypeMismatches(matrixInfo).ifPresent(reports::add);
        findGroupedHeader(matrixInfo).ifPresent(reports::add);

        return reports;
    }

    private Optional<ColumnTypeMismatchReportDto> findColumnTypeMismatches(MatrixInfo matrixInfo) {
        List<Integer> mismatches = matrixInfoService.checkTypeMismatch(matrixInfo);
        if (mismatches.isEmpty()) return Optional.empty();
        return Optional.of(new ColumnTypeMismatchReportDto().columnIndex(mismatches));
    }

    private Optional<MergeableColumnsReportDto> findMergeableColumns(MatrixInfo matrixInfo) {
        return Optional.empty();
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
