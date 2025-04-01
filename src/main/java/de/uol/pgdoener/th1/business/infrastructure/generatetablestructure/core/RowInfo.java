package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RowInfo {
    private final int rowId;
    private final List<ColumnInfo> columnInfos = new ArrayList<>();
    private String headerName;

    public RowInfo(int rowId) {
        this.rowId = rowId;
    }

    public void addColumnInfo(ColumnInfo columnInfo) {
        columnInfos.add(columnInfo);
    }

    public int countEntries() {
        return (int) columnInfos.stream().filter(ColumnInfo::hasEntry).count();
    }

    public List<Integer> getEmptyPositions() {
        return columnInfos.stream().filter(c -> !c.hasEntry()).map(ColumnInfo::getColumnId).toList();
    }

    public List<Integer> getFilledPositions() {
        return columnInfos.stream()
                .filter(ColumnInfo::hasEntry)
                .map(ColumnInfo::getColumnId)
                .toList();

    }
}