package de.uol.pgdoener.th1.domain.dataframe;

import java.time.LocalDate;

class DateColumn extends DataColumn {
    private final LocalDate[] data;
    private int pos = 0;

    public DateColumn(String name, int capacity) {
        super(name);
        data = new LocalDate[capacity];
    }

    @Override
    public void addValue(String value) {
        data[pos++] = LocalDate.parse(value); // erwartet Format yyyy-MM-dd
        size++;
    }

    @Override
    public Object getValue(int index) {
        return data[index];
    }
}
