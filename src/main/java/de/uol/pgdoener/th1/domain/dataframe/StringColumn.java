package de.uol.pgdoener.th1.domain.dataframe;

class StringColumn extends DataColumn {
    private final String[] data;
    private int pos = 0;

    public StringColumn(String name, int capacity) {
        super(name);
        data = new String[capacity];
    }

    public void addValue(String value) {
        data[pos++] = value;
        size++;
    }

    public Object getValue(int index) {
        return data[index];
    }
}