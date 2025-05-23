package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.analyze;

import de.uol.pgdoener.th1.business.dto.MergeableColumnsReportDto;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ColumnInfoService;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.MatrixInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindMergableColumnsService {

    private final ColumnInfoService columnInfoService;

    public Optional<List<MergeableColumnsReportDto>> find(MatrixInfo matrixInfo) {
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
}
