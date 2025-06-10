package de.uol.pgdoener.th1.business.service.query;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OldViewBuilderService {

    public String buildMaterializedViewAndTriggers(QueryRequest request, String viewName) {
        StringBuilder finalSql = new StringBuilder();

        // --- 1. View-Query generieren
        finalSql.append("DROP MATERIALIZED VIEW IF EXISTS ").append(viewName).append(";\n");
        finalSql.append(buildCreateViewQuery(request, viewName)).append("\n\n");

        // --- 2. Funktion & Trigger generieren
        finalSql.append(buildRefreshFunctionAndTriggers(viewName, request));

        return finalSql.toString();
    }

    private String buildCreateViewQuery(QueryRequest request, String viewName) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE MATERIALIZED VIEW ").append(viewName).append(" AS SELECT ");
        sql.append(String.join(", ", request.select));
        sql.append(" FROM ").append(request.table);

        if (request.joins != null) {
            for (QueryRequest.Join join : request.joins) {
                sql.append(" JOIN ").append(join.table)
                        .append(" ON ").append(request.table).append(".").append(join.sourceColumn)
                        .append(" = ").append(join.table).append(".").append(join.targetColumn);
            }
        }

        if (request.filters != null && !request.filters.isEmpty()) {
            sql.append(" WHERE ");
            List<String> whereClauses = request.filters.stream()
                    .map(f -> f.column + " " + f.operator + " " + f.value)
                    .collect(Collectors.toList());
            sql.append(String.join(" AND ", whereClauses));
        }

        if (request.groupBy != null && !request.groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", request.groupBy));
        }

        if (request.aggregations != null) {
            List<String> havingClauses = request.aggregations.stream()
                    .filter(a -> a.having != null)
                    .map(a -> a.agg + "(" + a.column + ") " + a.having.operator + " " + a.having.value)
                    .collect(Collectors.toList());
            if (!havingClauses.isEmpty()) {
                sql.append(" HAVING ").append(String.join(" AND ", havingClauses));
            }
        }

        if (request.orderBy != null && !request.orderBy.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(request.orderBy.stream()
                    .map(o -> o.column + " " + o.direction)
                    .collect(Collectors.joining(", ")));
        }

        return sql.toString();
    }

    private String buildRefreshFunctionAndTriggers(String viewName, QueryRequest request) {
        StringBuilder sql = new StringBuilder();

        String functionName = "refresh_view_" + viewName;

        // Funktion zum Aktualisieren der View
        sql.append("CREATE OR REPLACE FUNCTION ").append(functionName).append("() RETURNS trigger AS $$\n")
                .append("BEGIN\n")
                .append("    REFRESH MATERIALIZED VIEW CONCURRENTLY ").append(viewName).append(";\n")
                .append("    RETURN NULL;\n")
                .append("END;\n")
                .append("$$ LANGUAGE plpgsql;\n\n");

        // Tabellen bestimmen
        Set<String> tables = new HashSet<>();
        tables.add(request.table);
        if (request.joins != null) {
            for (QueryRequest.Join join : request.joins) {
                tables.add(join.table);
            }
        }

        // Trigger pro Tabelle
        for (String table : tables) {
            String triggerName = "trigger_refresh_" + viewName + "_on_" + table;
            sql.append("DROP TRIGGER IF EXISTS ").append(triggerName).append(" ON ").append(table).append(";\n");
            sql.append("CREATE TRIGGER ").append(triggerName).append("\n")
                    .append("AFTER INSERT OR UPDATE OR DELETE ON ").append(table).append("\n")
                    .append("FOR EACH STATEMENT EXECUTE FUNCTION ").append(functionName).append("();\n\n");
        }

        return sql.toString();
    }
}
