package de.uol.pgdoener.th1.domain.dataframe;

class BooleanColumn extends DataColumn {
    private final boolean[] data;
    private int pos = 0;

    public BooleanColumn(String name, int capacity) {
        super(name);
        data = new boolean[capacity];
    }

    @Override
    public void addValue(String value) {
        data[pos++] = Boolean.parseBoolean(value);
        size++;
    }

    @Override
    public Object getValue(int index) {
        return data[index];
    }
}