package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a matrix structure composed of multiple rows (RowInfo).
 * Provides utilities for validation and structure analysis.
 */
@Slf4j
@Getter
public class MatrixInfo {
    private final List<RowInfo> rowInfos = new ArrayList<>();

    /**
     * Adds a new row information to the matrix.
     *
     * @param rowInfo the row information to be added.
     */
    public void addRowInfo(RowInfo rowInfo) {
        rowInfos.add(rowInfo);
        log.debug("Added Row with ID: {}", rowInfo.getRowId());
    }

    /**
     * Returns the next row index, typically used to assign a new row ID.
     */
    public int getStartRow() {
        return rowInfos.size();
    }

    /**
     * Returns the next row index, typically used to assign a new row ID.
     */
    public List<Integer> getRowIndexes() {
        List<Integer> rowIndexes = new ArrayList<>();

        for (int i = 0; i < rowInfos.size() - 1; i++) {
            rowIndexes.add(rowInfos.get(i).getRowId());
        }
        return rowIndexes;
    }

    /**
     * Calculates the maximum number of columns across all rows.
     */
    public int getMaxRowSize() {
        int maxRowSize = 0;
        for (RowInfo rowInfo : rowInfos) {
            maxRowSize = Math.max(maxRowSize, rowInfo.getFilledPositionsSize());
        }
        return maxRowSize;
    }

    /**
     * Returns a list of row IDs that are partially filled but not complete.
     */
    public List<Integer> getRowToFill() {
        List<Integer> result = new ArrayList<>();
        int maxRowSize = getMaxRowSize();

        for (RowInfo rowInfo : rowInfos) {
            if (rowInfo.hasRowToFill(maxRowSize)) {
                result.add(rowInfo.getRowId());
            }
        }
        return result;
    }

    /**
     * Checks if there are any rows that are incomplete (partially filled).
     */
    public boolean hasEmptyRow() {
        return !getRowToFill().isEmpty();
    }

    /**
     * Identifies column indexes where only one row has an entry.
     * Note: currently simplified to always return 0.
     */
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

    /**
     * Builds the header names for the table including a final "Anzahl" column.
     * Rotates the last element to the beginning.
     */
    public List<String> getHeaderNames() {
        List<String> headerNames = new ArrayList<>(rowInfos.stream().map(RowInfo::getHeaderName).toList());

        if (!headerNames.isEmpty()) {
            String lastElement = headerNames.removeLast();
            headerNames.addFirst(lastElement); // An den Anfang setzen
        }
        headerNames.add("Anzahl");
        return headerNames;
    }

    /**
     * Checks whether the table header should be considered grouped.
     */
    public boolean hasGroupedHeader() {
        return rowInfos.size() > 1 && rowInfos.size() <= 5;
    }
}
