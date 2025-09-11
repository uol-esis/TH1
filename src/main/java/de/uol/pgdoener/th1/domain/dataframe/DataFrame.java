package de.uol.pgdoener.th1.domain.dataframe;

import java.util.LinkedHashMap;
import java.util.Set;

public class DataFrame {
    private final LinkedHashMap<String, DataColumn> columns = new LinkedHashMap<>();
    private int rowCount = 0;

    public void addColumn(String name, DataColumn col) {
        columns.put(name, col);
    }

    public void addRow(String[] row) {
        if (row.length != columns.size()) throw new RuntimeException("Row length mismatch!");
        int i = 0;
        for (DataColumn col : columns.values()) {
            col.addValue(row[i++]);
        }
        rowCount++;
    }

    public DataColumn getColumn(String name) {
        return columns.get(name);
    }

    public Set<String> getColumnNames() {
        return columns.keySet();
    }

    public int getRowCount() {
        return columns.isEmpty() ? 0 : columns.values().iterator().next().getSize();
    }

    public Object[] getRow(int index) {
        Object[] row = new Object[columns.size()];
        int i = 0;
        for (DataColumn col : columns.values()) {
            row[i++] = col.getValue(index);
        }
        return row;
    }
}
