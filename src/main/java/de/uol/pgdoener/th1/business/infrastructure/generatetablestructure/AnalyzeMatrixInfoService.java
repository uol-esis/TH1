package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.dto.ReportDto;
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

    private final MatrixInfoService matrixInfoService;

    public List<ReportDto> analyze(MatrixInfo matrixInfo) {
        List<ReportDto> reports = new ArrayList<>();

        findColumnTypeMismatches(matrixInfo).ifPresent(reports::add);

        return reports;
    }

    private Optional<ColumnTypeMismatchReportDto> findColumnTypeMismatches(MatrixInfo matrixInfo) {
        List<Integer> mismatches = matrixInfoService.checkTypeMismatch(matrixInfo);
        if (mismatches.isEmpty()) return Optional.empty();
        return Optional.of(new ColumnTypeMismatchReportDto().columnIndex(mismatches));
    }

}
