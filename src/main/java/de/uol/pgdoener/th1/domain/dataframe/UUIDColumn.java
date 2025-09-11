package de.uol.pgdoener.th1.domain.dataframe;

import java.util.UUID;

class UUIDColumn extends DataColumn {
    private final UUID[] data;
    private int pos = 0;

    public UUIDColumn(String name, int capacity) {
        super(name);
        data = new UUID[capacity];
    }

    @Override
    public void addValue(String value) {
        data[pos++] = UUID.fromString(value);
        size++;
    }

    @Override
    public Object getValue(int index) {
        return data[index];
    }
}
