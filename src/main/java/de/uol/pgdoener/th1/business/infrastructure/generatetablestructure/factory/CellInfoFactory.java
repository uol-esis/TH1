package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.factory;

import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core.ValueType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CellInfoFactory {

    public CellInfo create(int rowIndex, int colIndex, String entry) {
        ValueType valueType = detectType(entry);

        return new CellInfo(rowIndex, colIndex, entry, valueType);
    }

    private ValueType detectType(String entry) {

        if (entry == null) return ValueType.NULL;
        if (entry.isBlank()) return ValueType.EMPTY;
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
