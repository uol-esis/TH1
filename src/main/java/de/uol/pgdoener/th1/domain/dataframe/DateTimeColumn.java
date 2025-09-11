package de.uol.pgdoener.th1.domain.dataframe;

import java.time.LocalDateTime;

class DateTimeColumn extends DataColumn {
    private final LocalDateTime[] data;
    private int pos = 0;

    public DateTimeColumn(String name, int capacity) {
        super(name);
        data = new LocalDateTime[capacity];
    }

    @Override
    public void addValue(String value) {
        data[pos++] = LocalDateTime.parse(value); // erwartet Format yyyy-MM-ddTHH:mm:ss
        size++;
    }

    @Override
    public Object getValue(int index) {
        return data[index];
    }
}
