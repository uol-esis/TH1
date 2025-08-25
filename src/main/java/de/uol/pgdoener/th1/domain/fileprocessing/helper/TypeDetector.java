package de.uol.pgdoener.th1.domain.fileprocessing.helper;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TypeDetector {

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[a-fA-F0-9]{8}-([a-fA-F0-9]{4}-){3}[a-fA-F0-9]{12}$");
    private static final Pattern TIMESTAMP_PATTERN =
            Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}|\\d{14})$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?(%|€|CHF)?$");
    private static final Pattern TEXT_PATTERN = Pattern.compile(".*[a-zA-Z].*");
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "^(" +
                    "\\d{1,2}[./-]\\d{1,2}[./-]\\d{2,4}" +      // dd.MM.yyyy, dd/MM/yyyy, dd-MM-yyyy, d/M/yyyy, yy.MM.dd
                    "|" +
                    "\\d{4}[./-]\\d{1,2}[./-]\\d{1,2}" +        // yyyy.MM.dd, yyyy-MM-dd, yyyy/MM/dd
                    "|" +
                    "\\d{1,2}-[a-zA-Z]{3}-\\d{4}" +             // dd-MMM-yyyy
                    ")$"
    );

    public ValueType detect(String raw) {
        if (raw == null || raw.isBlank()) {
            return ValueType.TEXT;
        }
        raw = raw.trim();
        int length = raw.length();

        if (isDate(raw, length)) return ValueType.DATE;

        if (isUUID(raw, length)) return ValueType.UUID;

        if (isTimestamp(raw, length)) return ValueType.TIMESTAMP;

        if (isText(raw, length)) return ValueType.TEXT;

        return ValueType.NUMBER;
    }

    private boolean isDate(String s, int length) {
        if (length < 8 || length > 11) return false;
        if (!s.contains(".") && !s.contains("/") && !s.contains("-")) return false;

        return DATE_PATTERN.matcher(s).matches();
    }

    private boolean isUUID(String s, int length) {
        if (length != 36) return false;
        if (!s.contains("-")) return false;

        return UUID_PATTERN.matcher(s).matches();
    }

    private boolean isTimestamp(String s, int length) {
        if (length != 19 && length != 14) return false;

        return TIMESTAMP_PATTERN.matcher(s).matches();
    }

    private boolean isText(String s, int length) {
        return TEXT_PATTERN.matcher(s).matches();
    }

}
