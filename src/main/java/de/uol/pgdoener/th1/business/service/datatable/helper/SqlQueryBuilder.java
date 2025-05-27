package de.uol.pgdoener.th1.business.service.datatable.helper;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SqlQueryBuilder {
    String sql = "INSERT INTO :table VALUES (:table)";

    public String buildCreateTableQuery(String tableName, Map<String, String> columns) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (");
        columns.forEach((col, type) -> query.append("\"").append(col).append("\" ").append(type).append(", "));
        //query.append("PRIMARY KEY (\"id\")");
        //query.append(")");
        query.setLength(query.length() - 2);
        query.append(")");
        return query.toString();
    }

    public String buildInsertQuery(String tableName, Map<String, String> columns) {
        String[] headers = columns.keySet().stream().filter(key -> !key.equals("id")).toArray(String[]::new);

        StringBuilder insertQuery = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(", ", headers))
                .append(") VALUES ");

        StringJoiner valuesSql = new StringJoiner(", ");

        for (String row : headers) {
            String placeholders = String.join(", ", Collections.nCopies(headers.length, "?"));
            valuesSql.add("(" + placeholders + ")");
            break;
        }

        insertQuery.append(valuesSql);

        return insertQuery.toString();
    }

    public String buildInsertQueryNew(String tableName, Map<String, String> columns, int batchSize) {
        String[] headers = columns.keySet().stream().filter(key -> !key.equals("id")).toArray(String[]::new);

        StringBuilder insertQuery = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(", ", headers))
                .append(") VALUES ");

        StringJoiner valuesSql = new StringJoiner(", ");

        String placeholders = "(" + String.join(", ", Collections.nCopies(headers.length, "?")) + ")";
        for (int i = 0; i < batchSize; i++) {
            valuesSql.add(placeholders);
        }

        insertQuery.append(valuesSql);

        return insertQuery.toString();
    }


    public List<String> buildAlterTableQueries(String tableName, Set<String> existingColumns, Map<String, String> newColumns) {
        List<String> alterStatements = new ArrayList<>();

        // 2. Prüfe jede Spalte aus dem neuen Datensatz
        for (Map.Entry<String, String> entry : newColumns.entrySet()) {
            String columnName = entry.getKey();
            String columnType = entry.getValue();

            // 3. Wenn die Spalte noch nicht existiert → füge sie hinzu
            if (!existingColumns.contains(columnName.toLowerCase())) {
                String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s",
                        tableName, columnName, columnType);
                alterStatements.add(alterSql);
            }
        }

        return alterStatements;
    }

}
