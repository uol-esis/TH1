package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlType;
import org.springframework.stereotype.Component;

import static de.uol.pgdoener.th1.domain.datatable.helper.SqlTypePattern.*;

@Component
public class SqlTypeGuesser {

    public SqlType guessType(String value) {
        if (value == null || value.equalsIgnoreCase("NULL") || "*".equals(value)) {
            return SqlType.UNDEFINED;
        }
        if (INTEGER.matcher(value).matches()) return SqlType.INTEGER;
        if (DECIMAL.matcher(value).matches()) return SqlType.NUMERIC;
        if (BOOLEAN.matcher(value).matches()) return SqlType.BOOLEAN;
        if (DATE.matcher(value).matches()) return SqlType.DATE;
        if (TIMESTAMP.matcher(value).matches()) return SqlType.TIMESTAMP;
        if (UUID.matcher(value).matches()) return SqlType.UUID;
        return SqlType.TEXT;
    }
}
