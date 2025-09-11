package de.uol.pgdoener.th1.domain.dataframe;

class DoubleColumn extends DataColumn {
    private final double[] data;
    private int pos = 0;

    public DoubleColumn(String name, int capacity) {
        super(name);
        data = new double[capacity];
    }

    @Override
    public void addValue(String value) {
        data[pos++] = Double.parseDouble(value);
        size++;
    }

    @Override
    public Object getValue(int index) {
        return data[index];
    }
}
