package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlType;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import static de.uol.pgdoener.th1.domain.datatable.helper.SqlTypePattern.DATE;


@Component
public class SqlValueFormatter {

    public Object format(String value, SqlType columnType) {
        if ("*".equals(value) || "NULL".equals(value)) {
            return null;
        }
        try {
            return switch (columnType) {
                case INTEGER, SERIAL_PRIMARY_KEY -> Integer.valueOf(value);
                case NUMERIC -> Double.valueOf(value);
                case BOOLEAN -> Boolean.parseBoolean(value);
                case TEXT, UNDEFINED -> value;
                case DATE -> {
                    if (DATE.matcher(value).matches()) {
                        yield Date.valueOf(value);
                    }
                    yield value;
                }
                case UUID -> UUID.fromString(value);
                case TIMESTAMP -> Timestamp.valueOf(value);
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse value: " + value, e);
        }
    }
}
