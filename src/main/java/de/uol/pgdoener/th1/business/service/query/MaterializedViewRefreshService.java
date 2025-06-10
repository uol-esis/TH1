package de.uol.pgdoener.th1.business.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterializedViewRefreshService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Refresht alle Materialized Views in der Datenbank.
     */
    public void refreshAllMaterializedViews() {
        List<String> matViews = jdbcTemplate.queryForList(
                "SELECT matviewname FROM pg_matviews WHERE schemaname = 'public'", String.class);

        for (String viewName : matViews) {
            refreshMaterializedView(viewName);
        }
    }

    /**
     * Refresht eine Materialized View nach Name.
     *
     * @param viewName Name der Materialized View
     */
    public void refreshMaterializedView(String viewName) {
        String sql = "REFRESH MATERIALIZED VIEW CONCURRENTLY " + viewName;
        jdbcTemplate.execute(sql);
    }
}
