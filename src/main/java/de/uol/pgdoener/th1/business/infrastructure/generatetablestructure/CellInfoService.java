package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import org.springframework.stereotype.Service;

@Service
public class CellInfoService {

    public enum ValueType {
        INTEGER,
        DOUBLE,
        BOOLEAN,
        CHARACTER,
        STRING,
        NULL
    }

    public boolean isString(CellInfo cellInfo) {
        return detectType(cellInfo) == CellInfoService.ValueType.STRING;
    }

    public boolean hasEntry(CellInfo cellInfo) {
        return
    }

    public ValueType detectType(CellInfo cellInfo) {
        String entry = cellInfo.entry();

        if (entry == null) return ValueType.NULL;
        if (isInteger(entry)) return ValueType.INTEGER;
        if (isDouble(entry)) return ValueType.DOUBLE;
        if (isBoolean(entry)) return ValueType.BOOLEAN;
        if (isCharacter(entry)) return ValueType.CHARACTER;

        return ValueType.STRING;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    private boolean isCharacter(String s) {
        return s.length() == 1;
    }
}
