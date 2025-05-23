package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.GroupedHeaderReportDto;
import de.uol.pgdoener.th1.business.dto.ReportDto;
import de.uol.pgdoener.th1.business.dto.SumReportDto;
import de.uol.pgdoener.th1.business.dto.TableStructureGenerationSettingsDto;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.*;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
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

    private final FindGroupedHeaderService findGroupedHeaderService;
    private final FindEmptyRowService findEmptyRowService;
    private final FindEmptyColumnService findEmptyColumnService;
    private final FindEmptyHeaderService findEmptyHeaderService;
    private final FindSameAsHeaderService findSameAsHeaderService;
    private final FindColumnMismatchService findColumnMismatchService;
    private final FindMergableColumnsService findMergableColumnsService;
    private final FindSumService findSumReportService;

    public List<ReportDto> analyze(MatrixInfo matrixInfo, String[][] matrix, TableStructureGenerationSettingsDto settings) {
        List<ReportDto> reports = new ArrayList<>();

        Optional<GroupedHeaderReportDto> optionalGroupedHeaderReport = findGroupedHeaderService.find(matrixInfo, matrix);
        if (optionalGroupedHeaderReport.isPresent()) {
            reports.add(optionalGroupedHeaderReport.get());
            return reports;
        }
        Optional<SumReportDto> optionalSumReport = findSumReportService.find(matrixInfo, matrix, settings.getSumBlockList());
        if (optionalSumReport.isPresent()) {
            reports.add(optionalSumReport.get());
            return reports;
        }
        findColumnMismatchService.find(matrixInfo).ifPresent(reports::add);
        findMergableColumnsService.find(matrixInfo).ifPresent(reports::addAll);
        findEmptyRowService.find(matrixInfo).ifPresent(reports::add);
        findEmptyColumnService.find(matrixInfo).ifPresent(reports::add);
        findEmptyHeaderService.find(matrixInfo).ifPresent(reports::add);
        findSameAsHeaderService.find(matrixInfo, matrix).ifPresent(reports::add);

        return reports;
    }

}
