package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.finder;

import de.uol.pgdoener.th1.business.dto.EmptyRowReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.RowInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.RowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindEmptyRowService {

    private final RowInfoService rowInfoService;

    public Optional<EmptyRowReportDto> find(MatrixInfo matrixInfo) {
        List<RowInfo> rowInfos = matrixInfo.rowInfos();

        List<Integer> emptyRowIndices = rowInfos.stream()
                .filter(rowInfoService::isEmpty)
                .map(RowInfo::rowIndex)
                .toList();

        if (emptyRowIndices.isEmpty()) {
            return Optional.empty();
        }
        EmptyRowReportDto emptyRowReport = new EmptyRowReportDto()
                .rowIndex(emptyRowIndices);
        return Optional.of(emptyRowReport);
    }

}
