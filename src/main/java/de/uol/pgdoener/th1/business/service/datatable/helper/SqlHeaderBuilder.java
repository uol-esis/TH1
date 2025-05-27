package de.uol.pgdoener.th1.business.service.datatable.helper;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SqlHeaderBuilder {

    /**
     * Extracts the header from the first row of the matrix and infers SQL types based on the first data row.
     */
    public Map<String, String> build(String[][] matrix) {
        String[] headers = matrix[0];
        String[] firstDataRow = matrix[1];

        if (headers.length != firstDataRow.length) {
            throw new IllegalArgumentException("Header and data row length mismatch.");
        }

        Map<String, String> columnHeaders = new LinkedHashMap<>();
        columnHeaders.put("id", "SERIAL PRIMARY KEY");
        //columnHeaders.put("id", "UUID PRIMARY KEY DEFAULT uuid_generate_v4()");

        for (int i = 0; i < headers.length; i++) {
            String header = prepareForSQLColumnName(headers[i]);
            String value = firstDataRow[i].trim();
            String type = guessType(value);
            columnHeaders.put(header, type);
        }

        return columnHeaders;
    }

    //TODO: Error Handeling for column names. Better handeling von database errors
    // Fall abfangen wenn datentyp leerer string
    private String prepareForSQLColumnName(String input) {
        if (input == null || input.isEmpty()) {
            return "_";
        }

        // Trim und Kleinschreibung
        String normalized = input.trim().toLowerCase();
        // Entfernt alle nicht-ASCII-Zeichen (z. B. Umlaute → ue)
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        // Ersetzt ungültige Zeichen durch Unterstrich
        normalized = normalized.replaceAll("[^a-z0-9_]", "_");
        // Entfernt doppelte Unterstriche
        normalized = normalized.replaceAll("_+", "_");

        // Stellt sicher, dass der Name mit einem Buchstaben beginnt
        if (!Character.isLetter(normalized.charAt(0))) {
            normalized = "_" + normalized;
        }

        return normalized;
    }

    private String guessType(String value) {
        if (value.matches("\\d+")) return "INTEGER"; // Ganzzahlen
        if (value.matches("\\d+\\.\\d+")) return "NUMERIC"; // Dezimalzahlen
        if (value.matches("true|false")) return "BOOLEAN"; // Wahrheitswerte
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) return "DATE"; // Datumsangaben im Format YYYY-MM-DD
        if (value.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) return "DATE"; // Datumsangaben im Format dd.MM.yyyy
        if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")) return "TIMESTAMP"; // ISO-8601-Timestamp
        if (value.matches("[A-Fa-f0-9]{32}")) return "UUID"; // UUIDs (hexadezimale 32-Zeichen-Strings)
        return "TEXT"; // Standard-Texttyp
    }

}
