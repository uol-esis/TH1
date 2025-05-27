package de.uol.pgdoener.th1.data.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/// TODO: Sql injection
@Repository
@AllArgsConstructor
public class DynamicTableRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void executeRawSql(String query) {
        jdbcTemplate.execute(query);
    }

    public void executeRawSqlWithBatch(String sqlWithPlaceholders, List<Object[]> batchParameters) {
        jdbcTemplate.batchUpdate(sqlWithPlaceholders, batchParameters);
    }

    public void executeNamedSql(String sql, Map<String, Object> params) {
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public void executeUpdateRawSql(String query, List<Object> values) {
        jdbcTemplate.update(query, values.toArray());
    }

    public boolean tableExists(String tableName) {
        String sql = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                        AND table_name = ?
                    )
                """;

        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
        return Boolean.TRUE.equals(exists);
    }

    public Set<String> getExistingColumnNames(String tableName) {
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_name = ? AND table_schema = current_schema()";

        List<String> columnNames = jdbcTemplate.queryForList(sql, new Object[]{tableName}, String.class);
        return columnNames.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public List<String> getAllTableNames() {
        String sql = """
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                    AND table_type = 'BASE TABLE'
                    ORDER BY table_name
                """;
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Map<String, Object>> getTableMetadata() {
        String sql = """
                SELECT 
                    c.table_name,
                    c.column_name,
                    c.data_type,
                    c.is_nullable,
                    COALESCE(tc.constraint_type, 'NONE') AS constraint_type
                FROM information_schema.columns c
                LEFT JOIN information_schema.key_column_usage kcu 
                    ON c.table_name = kcu.table_name 
                    AND c.column_name = kcu.column_name
                    AND c.table_schema = kcu.table_schema
                LEFT JOIN information_schema.table_constraints tc
                    ON kcu.constraint_name = tc.constraint_name
                    AND kcu.table_schema = tc.table_schema
                WHERE c.table_schema = 'public'
                ORDER BY c.table_name, c.ordinal_position
                """;

        return jdbcTemplate.queryForList(sql);
    }
}