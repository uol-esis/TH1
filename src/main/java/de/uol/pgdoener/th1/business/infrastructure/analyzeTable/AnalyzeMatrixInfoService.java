package de.uol.pgdoener.th1.business.infrastructure.analyzeTable;

import de.uol.pgdoener.th1.business.dto.*;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory.MatrixInfoFactory;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder.*;
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

    private final MatrixInfoFactory matrixInfoFactory;
    private final FindGroupedHeaderService findGroupedHeaderService;
    private final FindEmptyRowService findEmptyRowService;
    private final FindEmptyColumnService findEmptyColumnService;
    private final FindEmptyHeaderService findEmptyHeaderService;
    private final FindSameAsHeaderService findSameAsHeaderService;
    private final FindColumnMismatchServiceNew findColumnMismatchService;
    private final FindMergableColumnsService findMergableColumnsService;
    private final FindSumService findSumReportService;
    private final FindSplitRowService findSplitRowService;

    public List<ReportDto> analyze(String[][] matrix, TableStructureGenerationSettingsDto settings) {
        MatrixInfo matrixInfo = matrixInfoFactory.createParallel(matrix);

        List<ReportDto> reports = new ArrayList<>();

        Optional<GroupedHeaderReportDto> optionalGroupedHeaderReport = findGroupedHeaderService.find(matrixInfo, matrix);
        if (optionalGroupedHeaderReport.isPresent()) {
            reports.add(optionalGroupedHeaderReport.get());
            return reports;
        }
        Optional<List<SplitRowReportDto>> optionalSplitRowReport = findSplitRowService.find(matrixInfo, matrix);
        if (optionalSplitRowReport.isPresent()) {
            reports.addAll(optionalSplitRowReport.get());
            return reports;
        }
        Optional<SumReportDto> optionalSumReport = findSumReportService.find(matrixInfo, matrix, settings.getSumBlockList());
        if (optionalSumReport.isPresent()) {
            reports.add(optionalSumReport.get());
            return reports;
        }
        findColumnMismatchService.find(matrixInfo, matrix).ifPresent(reports::add);
        findMergableColumnsService.find(matrixInfo).ifPresent(reports::addAll);
        findEmptyRowService.find(matrixInfo).ifPresent(reports::add);
        findEmptyColumnService.find(matrixInfo).ifPresent(reports::add);
        findEmptyHeaderService.find(matrixInfo).ifPresent(reports::add);
        findSameAsHeaderService.find(matrixInfo, matrix).ifPresent(reports::add);

        return reports;
    }

}
