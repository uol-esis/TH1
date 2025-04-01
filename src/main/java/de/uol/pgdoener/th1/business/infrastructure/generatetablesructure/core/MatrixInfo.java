package de.uol.pgdoener.th1.business.infrastructure.generatetablesructure.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MatrixInfo {
    private final List<RowInfo> rowInfos = new ArrayList<>();

    public void addRowInfo(RowInfo rowInfo) {
        rowInfos.add(rowInfo);
    }

    public int getStartRow() {
        return rowInfos.size();
    }

    public List<Integer> getRowIndexes() {
        List<Integer> rowIndexes = new ArrayList<>();
        // Bis vor dem letzten Eintrag iterieren
        for (int i = 0; i < rowInfos.size() - 1; i++) {
            rowIndexes.add(rowInfos.get(i).getRowId());
        }
        return rowIndexes;
    }

    public List<Integer> getRowToFill() {
        List<Integer> rowIndexes = new ArrayList<>();
        int columnSize = rowInfos.getFirst().getColumnInfos().size();
        for (RowInfo rowInfo : rowInfos) {
            List<Integer> filledPositions = rowInfo.getFilledPositions();
            if (filledPositions.size() > 2 && filledPositions.size() < columnSize) {
                rowIndexes.add(rowInfo.getRowId());
            }
        }
        return rowIndexes;
    }

    public List<Integer> getColumnIndexes() {
        List<Integer> rowIndexes = new ArrayList<>();
        for (RowInfo rowInfo : rowInfos) {

            /// TODO: überarbeiten wenn mehr als eine spalte in der column aufgelöst werden muss.
            if (rowInfo.countEntries() == 1) {
                rowIndexes.add(0);
            }
        }
        return rowIndexes;
    }

    public List<String> getHeaderNames() {
        List<String> headerNames = new ArrayList<>(rowInfos.stream().map(RowInfo::getHeaderName).toList());

        if (!headerNames.isEmpty()) {
            String lastElement = headerNames.removeLast();
            headerNames.addFirst(lastElement); // An den Anfang setzen
        }
        headerNames.add("Anzahl");
        return headerNames;
    }

    public boolean isGroupedHeader() {
        return rowInfos.size() > 1 && rowInfos.size() <= 5;
    }
}
