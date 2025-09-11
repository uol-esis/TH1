package de.uol.pgdoener.th1.domain.dataframe;

class DataFrameBuilder {
    private final DataFrame df = new DataFrame();
    private final int rowCount;

    public DataFrameBuilder(int rowCount) {
        this.rowCount = rowCount;
    }

    public void initColumns(String[] headers, String[] firstRowSample) {
        for (int i = 0; i < headers.length; i++) {
            DataColumn col;
            String val = firstRowSample[i];
            if (val.matches("-?\\d+")) col = new IntColumn(headers[i], rowCount);
            else if (val.matches("-?\\d+\\.\\d+")) col = new DoubleColumn(headers[i], rowCount);
            else if (val.matches("true|false")) col = new BooleanColumn(headers[i], rowCount);
            else if (val.matches("\\d{4}-\\d{2}-\\d{2}")) col = new DateColumn(headers[i], rowCount);
            else if (val.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"))
                col = new DateTimeColumn(headers[i], rowCount);
            else if (val.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))
                col = new UUIDColumn(headers[i], rowCount);
            else col = new StringColumn(headers[i], rowCount);
            df.addColumn(headers[i], col);
        }
    }

    public void addRow(String[] row) {
        df.addRow(row);
    }

    public DataFrame build() {
        return df;
    }
}

