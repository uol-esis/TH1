package de.uol.pgdoener.th1.domain.datatable.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SqlValueBuilder {

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
                        formattedRow[i] = formatValue(value, columnType, columnName, columns);
                    }
                    System.out.println(Arrays.toString(formattedRow));
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
                Object formattedValue = formatValue(value, columnType, columnName, columns);
                values.add(formattedValue);
            }
        }
        return values;
    }

    private static Object formatValue(String value, String columnType, String columnName, Map<String, String> columns) {
        if ("*".equals(value)) {
            return null;
        }

        try {


            switch (columnType.toUpperCase()) {
                case "INTEGER":
                    return Integer.parseInt(value);
                case "NUMERIC":
                    return Double.valueOf(value);
                case "BOOLEAN":
                    return Boolean.parseBoolean(value);
                case "TEXT":
                case "VARCHAR":
                case "CHAR":
                    return value;
                case "DATE":
                    try {
                        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            return java.sql.Date.valueOf(value); // Bereits im richtigen Format
                        } else if (value.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            LocalDate date = LocalDate.parse(value, formatter);
                            return java.sql.Date.valueOf(date);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid date format for value: " + value);
                    }
                default:
                    throw new IllegalArgumentException("Unknown column type: " + columnType);
            }
        } catch (Exception e) {
            log.warn("Parsing error in column '{}'. Changing type from {} to TEXT. Offending value: '{}'",
                    columnName, columnType, value);

            columns.put(columnName, "TEXT");
            return value;
        }
    }

}
