package de.uol.pgdoener.th1.domain.datatable.helper;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SqlValueBuilder {

    private static final Pattern DATE_PATTERN_1 = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern DATE_PATTERN_2 = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");

    public List<Object[]> build(Map<String, String> columns, String[][] transformedMatrix) {
        String[] headers = columns.keySet().stream().filter(key -> !key.equals("id")).toArray(String[]::new);
        String[][] valueMatrix = Arrays.copyOfRange(transformedMatrix, 1, transformedMatrix.length);

        return Arrays.stream(valueMatrix)
                .map(row -> {
                    Object[] formattedRow = new Object[headers.length];
                    for (int i = 0; i < headers.length; i++) {
                        String columnName = headers[i];
                        String columnType = columns.get(columnName);
                        String value = row[i];
                        formattedRow[i] = formatValue(value, columnType);
                    }
                    return formattedRow;
                })
                .toList();
    }

    public List<Object> buildNew(Map<String, String> columns, String[][] transformedMatrix, int start, int end) {
        String[] headers = columns.keySet().stream().filter(key -> !key.equals("id")).toArray(String[]::new);
        String[][] valueMatrix = Arrays.copyOfRange(transformedMatrix, 1, transformedMatrix.length);

        List<Object> values = new ArrayList<>();
        for (int i = start; i < end; i++) {
            String[] row = valueMatrix[i];
            for (int j = 0; j < headers.length; j++) {
                String columnName = headers[j];
                String columnType = columns.get(columnName);
                String value = row[j];
                Object formattedValue = formatValue(value, columnType);
                values.add(formattedValue);
            }
        }
        return values;
    }

    private static Object formatValue(String value, String columnType) {
        if ("*".equals(value)) {
            return null;
        }

        return switch (columnType.toUpperCase()) {
            case "INTEGER" -> Integer.parseInt(value);
            case "NUMERIC" -> Double.valueOf(value);
            case "BOOLEAN" -> Boolean.parseBoolean(value);
            case "TEXT", "VARCHAR", "CHAR" -> value;
            case "DATE" -> {
                try {
                    if (DATE_PATTERN_1.matcher(value).matches()) {
                        yield java.sql.Date.valueOf(value);
                    } else if (DATE_PATTERN_2.matcher(value).matches()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        LocalDate date = LocalDate.parse(value, formatter);
                        yield java.sql.Date.valueOf(date);
                    }
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid date format for value: " + value, e);
                }
                throw new IllegalArgumentException("Invalid date format for value: " + value);
            }
            default -> throw new IllegalArgumentException("Unknown column type: " + columnType);
        };
    }

}
