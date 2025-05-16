package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ColumnInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ValueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ColumnInfoService {

    private final CellInfoService cellInfoService;

    /**
     * Checks if the given column has more than one type of cells.
     * The header is assumed to be a string and is ignored.
     *
     * @param columnInfo the column to check
     * @return true if there are more than one type, false otherwise
     */
    public boolean hasTypeMismatch(ColumnInfo columnInfo) {
        Map<ValueType, Integer> typeCounts = columnInfo.typeCounts();

        // find types present in column
        List<ValueType> presentTypes = Arrays.stream(ValueType.values())
                .filter(t -> typeCounts.containsKey(t) && typeCounts.get(t) > 0)
                .toList();

        // no mismatch if only one type present
        if (presentTypes.size() == 1) {
            return false;
        }

        // no mismatch if the only other type is string in the header
        if (presentTypes.size() == 2 && presentTypes.contains(ValueType.STRING) && typeCounts.get(ValueType.STRING) == 1) {
            return false;
        }

        // TODO handle compatible types like INTEGER and DOUBLE or STRING and CHARACTER
        return true;
    }

    /**
     * Checks if the given columns are mergeable.
     * Columns are mergeable if only one column in any given row has an entry.
     * The first row is ignored as it should be the header.
     *
     * @param columnInfos the columns to check
     * @return true if the columns are mergeable, false otherwise
     */
    public boolean areMergeable(List<ColumnInfo> columnInfos) {
        if (columnInfos.isEmpty()) {
            return false;
        }

        for (int i = 0; i < columnInfos.get(i).cellInfos().size(); i++) {
            if (!isRowMergeable(columnInfos, i)) return false;
        }

        return true;
    }

    // TODO check for type compatibility
    private boolean isRowMergeable(List<ColumnInfo> columnInfos, int rowIndex) {
        return columnInfos.stream()
                .filter(c -> !cellInfoService.isEmpty(c.cellInfos().get(rowIndex)))
                .count() == 1;
    }

}
