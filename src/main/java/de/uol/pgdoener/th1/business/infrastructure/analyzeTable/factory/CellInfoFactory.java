package de.uol.pgdoener.th1.business.infrastructure.analyzeTable.factory;

import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.CellInfo;
import de.uol.pgdoener.th1.business.infrastructure.analyzeTable.core.ValueType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CellInfoFactory {

    public CellInfo create(int rowIndex, int colIndex, String entry) {
        ValueType valueType = detectType(entry);

        return new CellInfo(rowIndex, colIndex, valueType);
    }

    private ValueType detectType(String entry) {

        if (entry.isBlank()) return ValueType.EMPTY;
        if (isDouble(entry)) return ValueType.NUMBER;
        if (isBoolean(entry)) return ValueType.BOOLEAN;

        return ValueType.STRING;
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

}
