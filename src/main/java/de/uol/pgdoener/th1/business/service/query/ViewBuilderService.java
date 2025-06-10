package de.uol.pgdoener.th1.business.service.query;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViewBuilderService {

    public String buildView(QueryRequest request, String viewName, boolean materialized) {
        StringBuilder sql = new StringBuilder();

        // View löschen, je nach Typ
        if (materialized) {
            sql.append("DROP MATERIALIZED VIEW IF EXISTS ").append(viewName).append(";\n");
        } else {
            sql.append("DROP VIEW IF EXISTS ").append(viewName).append(";\n");
        }

        // View erstellen, je nach Typ
        if (materialized) {
            sql.append("CREATE MATERIALIZED VIEW ");
        } else {
            sql.append("CREATE VIEW ");
        }
        sql.append(viewName).append(" AS SELECT ");
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

       /* if (request.orderBy != null && !request.orderBy.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(request.orderBy.stream()
                    .map(o -> o.column + " " + o.direction)
                    .collect(Collectors.joining(", ")));
        }*/

        sql.append(";");

        return sql.toString();
    }
}
