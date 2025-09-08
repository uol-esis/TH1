package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import de.uol.pgdoener.th1.domain.datatable.model.SqlType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SqlHeaderBuilder {

    private static final Pattern REPLACE_INVALID_CHARS_PATTERN = Pattern.compile("[^a-z0-9_]");
    private static final Pattern REPLACE_MULTIPLE_UNDERSCORES_PATTERN = Pattern.compile("_+");

    /**
     * Extracts the header from the first row of the matrix and infers SQL types based on the first data row.
     */
    public List<SqlColumn> build(String[][] matrix) {
        return Arrays.stream(matrix[0])
                .map(this::normalizeHeader)
                .map(h -> new SqlColumn(h, SqlType.UNDEFINED))
                .toList();
    }

    private String normalizeHeader(String s) {
        if (s == null || s.isBlank() || "*".equals(s)) {
            return "col_unknown";
        }
        return prepareForSQLColumnName(s);
    }

    private String prepareForSQLColumnName(String input) {
        String normalized = input.trim().toLowerCase();
        normalized = replaceUmlauts(normalized);
        normalized = REPLACE_INVALID_CHARS_PATTERN.matcher(normalized).replaceAll("_");
        normalized = REPLACE_MULTIPLE_UNDERSCORES_PATTERN.matcher(normalized).replaceAll("_");

        //first character: must be a letter or underscore
        if (!Character.isLetter(normalized.charAt(0)) && normalized.charAt(0) != '_') {
            normalized = "col_" + normalized;
        }

        // Maximum length 63 (PG-Ident limit)
        if (normalized.length() > 63) {
            String hash = Integer.toHexString(normalized.hashCode());
            normalized = normalized.substring(0, 63 - hash.length() - 1) + "_" + hash;
        }

        return normalized;
    }

    private String ensureUnique(String name, Map<String, Integer> seen) {
        int count = seen.getOrDefault(name, 0);
        if (count == 0) {
            seen.put(name, 1);
            return name;
        }
        String uniqueName = name + "_" + (count + 1);
        seen.put(name, count + 1);
        return uniqueName;
    }

    private String replaceUmlauts(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case 'ä' -> sb.append("ae");
                case 'ö' -> sb.append("oe");
                case 'ü' -> sb.append("ue");
                case 'ß' -> sb.append("ss");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
