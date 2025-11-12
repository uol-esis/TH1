package de.uol.pgdoener.th1.domain.analyzeTable.factory;

import de.uol.pgdoener.th1.domain.analyzeTable.model.CellInfo;
import de.uol.pgdoener.th1.domain.analyzeTable.model.ValueType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class CellInfoFactory {

    private static final Pattern DOUBLE_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public CellInfo create(int rowIndex, int colIndex, String entry) {
        ValueType valueType = detectType(entry);

        return new CellInfo(rowIndex, colIndex, valueType);
    }

    private ValueType detectType(String entry) {

        if (entry.equals("*")) return ValueType.NULL;
        if (entry.isBlank()) return ValueType.EMPTY;
        if (isDouble(entry)) return ValueType.NUMBER;
        if (isBoolean(entry)) return ValueType.BOOLEAN;

        return ValueType.STRING;
    }

    private boolean isDouble(String s) {
        if (!DOUBLE_PATTERN.matcher(s).matches()) {
            return false;
        }
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
