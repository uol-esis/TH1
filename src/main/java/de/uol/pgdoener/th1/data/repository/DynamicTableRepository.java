package de.uol.pgdoener.th1.data.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}