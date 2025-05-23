package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder;

import de.uol.pgdoener.th1.business.dto.ColumnTypeMismatchReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindColumnMismatchService {

    private final ColumnInfoService columnInfoService;

    public Optional<ColumnTypeMismatchReportDto> find(MatrixInfo matrixInfo) {
        List<Integer> mismatches = matrixInfo.columnInfos().stream()
                .filter(columnInfoService::hasTypeMismatch)
                .map(ColumnInfo::columnIndex)
                .toList();
        if (mismatches.isEmpty()) return Optional.empty();
        return Optional.of(new ColumnTypeMismatchReportDto().columnIndex(mismatches));
    }
}
