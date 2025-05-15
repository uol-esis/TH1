package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.ColumnTypeMismatchReport;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.analyze.Report;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.MatrixInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeMatrixInfoService {

    private final MatrixInfoService matrixInfoService;

    public List<Report> analyze(MatrixInfo matrixInfo) {
        List<Report> reports = new ArrayList<>();
        reports.addAll(findColumnTypeMismatches(matrixInfo));
        return reports;
    }

    private List<ColumnTypeMismatchReport> findColumnTypeMismatches(MatrixInfo matrixInfo) {

    }

}
