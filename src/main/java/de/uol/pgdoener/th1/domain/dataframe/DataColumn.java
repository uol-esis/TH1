package de.uol.pgdoener.th1.domain.dataframe;

public abstract class DataColumn {
    protected String name;
    protected int size = 0;

    public DataColumn(String name) {
        this.name = name;
    }

    public abstract void addValue(String value);

    public abstract Object getValue(int index);

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }
}
