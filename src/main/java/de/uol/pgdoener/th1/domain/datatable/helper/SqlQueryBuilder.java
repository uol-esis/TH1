package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SqlQueryBuilder {

    public String buildCreateTableQuery(String tableName, List<SqlColumn> columns) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (");

        for (SqlColumn column : columns) {
            query.append("\"")
                    .append(column.getName())
                    .append("\" ")
                    .append(column.getType().getSqlName()) // Enum zu String
                    .append(", ");
        }

        query.setLength(query.length() - 2);
        query.append(")");
        return query.toString();
    }

    public String buildInsertQuery(String tableName, List<SqlColumn> columns, List<Object[]> values) {
        String[] headers = getInsertableHeaders(columns, values.getFirst().length);

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

    private String[] getInsertableHeaders(List<SqlColumn> columns, int firstRowLength) {
        return columns.stream()
                .map(SqlColumn::getName)
                .filter(name -> !"id".equals(name) || firstRowLength == columns.size())
                .toArray(String[]::new);
    }

}
