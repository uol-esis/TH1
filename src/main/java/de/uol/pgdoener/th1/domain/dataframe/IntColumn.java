package de.uol.pgdoener.th1.domain.dataframe;

public class IntColumn extends DataColumn {
    private final int[] data;
    private int pos = 0;

    public IntColumn(String name, int capacity) {
        super(name);
        data = new int[capacity];
    }

    @Override
    public void addValue(String value) {
        data[pos++] = Integer.parseInt(value);
        size++;
    }

    @Override
    public Object getValue(int index) {
        return data[index];
    }
}
