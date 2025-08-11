package de.uol.pgdoener.th1.domain.datatable.helper;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Component
public class SqlHeaderBuilder {

    private static final Pattern REMOVE_NON_ASCII_PATTERN = Pattern.compile("\\p{M}");
    private static final Pattern REPLACE_INVALID_CHARS_PATTERN = Pattern.compile("[^a-z0-9_]");
    private static final Pattern REPLACE_MULTIPLE_UNDERSCORES_PATTERN = Pattern.compile("_+");

    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("\\d+\\.\\d+");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false");
    private static final Pattern DATE_PATTERN_1 = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern DATE_PATTERN_2 = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");
    private static final Pattern UUID_PATTERN = Pattern.compile("[A-Fa-f0-9]{32}");

    /**
     * Extracts the header from the first row of the matrix and infers SQL types based on the first data row.
     */
    public Map<String, String> build(String[][] matrix) {
        String[] headers = matrix[0];

        Map<String, String> columnHeaders = new LinkedHashMap<>();
        columnHeaders.put("id", "SERIAL PRIMARY KEY");

        for (int i = 0; i < headers.length; i++) {
            String header = prepareForSQLColumnName(headers[i]);
            String value = getValue(i, matrix);
            String type = guessType(value);
            columnHeaders.put(header, type);
        }

        return columnHeaders;
    }

    // private methods //

    private String getValue(int i, String[][] matrix) {
        for (int rowIndex = 1; rowIndex < matrix.length; rowIndex++) {
            String value = matrix[rowIndex][i];
            if (value.equals("*")) continue;
            return value;
        }
        throw new NoSuchElementException("No valid (non-*) value found in column index " + i);
    }

    private String prepareForSQLColumnName(String input) {
        if (input == null || input.isEmpty()) {
            return "_";
        }

        // Trim und Kleinschreibung
        String normalized = input.trim().toLowerCase();
        // Entfernt alle nicht-ASCII-Zeichen (z. B. Umlaute → ue)
        normalized = REMOVE_NON_ASCII_PATTERN.matcher(Normalizer.normalize(normalized, Normalizer.Form.NFD)).replaceAll("");
        // Ersetzt ungültige Zeichen durch Unterstrich
        normalized = REPLACE_INVALID_CHARS_PATTERN.matcher(normalized).replaceAll("_");
        // Entfernt doppelte Unterstriche
        normalized = REPLACE_MULTIPLE_UNDERSCORES_PATTERN.matcher(normalized).replaceAll("_");

        // Stellt sicher, dass der Name mit einem Buchstaben beginnt
        if (!Character.isLetter(normalized.charAt(0))) {
            normalized = "_" + normalized;
        }

        return normalized;
    }

    private String guessType(String value) {
        if (INTEGER_PATTERN.matcher(value).matches()) return "INTEGER"; // Ganzzahlen
        if (DECIMAL_PATTERN.matcher(value).matches()) return "NUMERIC"; // Dezimalzahlen
        if (BOOLEAN_PATTERN.matcher(value).matches()) return "BOOLEAN"; // Wahrheitswerte
        if (DATE_PATTERN_1.matcher(value).matches()) return "DATE"; // Datumsangaben im Format YYYY-MM-DD
        if (DATE_PATTERN_2.matcher(value).matches()) return "DATE"; // Datumsangaben im Format dd.MM.yyyy
        if (TIMESTAMP_PATTERN.matcher(value).matches()) return "TIMESTAMP"; // ISO-8601-Timestamp
        if (UUID_PATTERN.matcher(value).matches()) return "UUID"; // UUIDs (hexadezimale 32-Zeichen-Strings)
        return "TEXT"; // Standard-Texttyp
    }

}
