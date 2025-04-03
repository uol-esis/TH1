package de.uol.pgdoener.th1.data.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.Normalizer;
import java.util.*;

/// TODO: Sql injection
@Repository
public class DynamicTableRepository {
    private final JdbcTemplate jdbcTemplate;

    public DynamicTableRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists(String tableName, String[][] transformedMatrix) {
        Map<String, String> header = getHeader(transformedMatrix);

        StringBuilder createQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        header.forEach((column, type) -> createQuery.append("\"").append(column).append("\" ").append(type).append(", "));
        createQuery.delete(createQuery.length() - 2, createQuery.length()); // Entferne letztes Komma
        createQuery.append(")");
        jdbcTemplate.execute(createQuery.toString());
    }

    public void insertData(String tableName, String[][] transformedMatrix) {
        Map<String, String> headerTypes = getHeader(transformedMatrix);
        String[] headers = headerTypes.keySet().stream().skip(1).toArray(String[]::new);
        String[][] valueMatrix = Arrays.copyOfRange(transformedMatrix, 1, transformedMatrix.length);

        StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (");

        // Spaltennamen hinzufügen
        for (String header : headers) {
            insertQuery.append(header).append(", ");
        }
        insertQuery.delete(insertQuery.length() - 2, insertQuery.length()); // Letztes Komma entfernen

        insertQuery.append(") VALUES ");
        List<Object> values = new ArrayList<>();

        for (String[] row : valueMatrix) {
            insertQuery.append("(");
            for (int i = 0; i < row.length; i++) {
                String columnName = headers[i];
                String columnType = headerTypes.get(columnName);

                Object formattedValue = formatValue(row[i], columnType);
                values.add(formattedValue);

                insertQuery.append("?, "); // Platzhalter für Prepared Statement
            }
            insertQuery.delete(insertQuery.length() - 2, insertQuery.length()); // Letztes Komma entfernen
            insertQuery.append("), ");
        }
        insertQuery.delete(insertQuery.length() - 2, insertQuery.length());// Letztes Komma entfernen
        jdbcTemplate.update(insertQuery.toString(), values.toArray());
    }

    private static String prepareForSQLColumnName(String input) {
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

    private Map<String, String> getHeader(String[][] transformedMatrix) {
        Map<String, String> columnHeaders = new LinkedHashMap<>();
        columnHeaders.put("id", "SERIAL");

        String[] headers = transformedMatrix[0];
        String[] firstRecord = transformedMatrix[1];

        for (int i = 0; i < headers.length; i++) {
            //String header = headers[i].trim().toLowerCase().replaceAll("\\s+", "");
            String header = prepareForSQLColumnName(headers[i]);
            String value = firstRecord[i].trim();
            String type = guessType(value);
            columnHeaders.put(header, type);
        }
        return columnHeaders;
    }

    private static String guessType(String value) {
        if (value.matches("\\d+")) return "INTEGER"; // Ganzzahlen
        if (value.matches("\\d+\\.\\d+")) return "NUMERIC"; // Dezimalzahlen
        if (value.matches("true|false")) return "BOOLEAN"; // Wahrheitswerte
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) return "DATE"; // Datumsangaben im Format YYYY-MM-DD
        if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")) return "TIMESTAMP"; // ISO-8601-Timestamp
        if (value.matches("[A-Fa-f0-9]{32}")) return "UUID"; // UUIDs (hexadezimale 32-Zeichen-Strings)
        return "TEXT"; // Standard-Texttyp
    }

    // Hilfsmethode, um Werte basierend auf dem Datentyp zu formatieren
    private static Object formatValue(String value, String columnType) {
        switch (columnType.toUpperCase()) {
            case "INTEGER":
                return Integer.parseInt(value);
            case "NUMERIC":
                return Double.parseDouble(value);
            case "BOOLEAN":
                return Boolean.parseBoolean(value);
            case "TEXT":
            case "VARCHAR":
            case "CHAR":
                return value;
            case "DATE":
                try {
                    return java.sql.Date.valueOf(value); // Konvertiere in Date (im Format yyyy-MM-dd)
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid date format for value: " + value);
                }
            default:
                throw new IllegalArgumentException("Unknown column type: " + columnType);
        }
    }
}