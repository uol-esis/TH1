package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.business.service.query.MaterializedViewRefreshService;
import de.uol.pgdoener.th1.business.service.query.QueryRequest;
import de.uol.pgdoener.th1.business.service.query.ViewBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/view")
@RequiredArgsConstructor
public class QueryController {

    private final JdbcTemplate jdbcTemplate;
    private final ViewBuilderService viewBuilderService;
    private final MaterializedViewRefreshService materializedViewRefreshService;

    @PostMapping("/generate")
    public String createMaterializedViewWithTriggers(
            @RequestParam String viewName,
            @RequestBody QueryRequest request
    ) {
        String sql = viewBuilderService.buildView(request, viewName, true);
        System.out.println(sql);
        jdbcTemplate.execute(sql);
        return "Materialized view and triggers created successfully.\n\nGenerated SQL:\n" + sql;
    }

    @PostMapping("/update")
    public String updateMaterializedView(
            @RequestParam String viewName
    ) {
        materializedViewRefreshService.refreshMaterializedView(viewName);
        return "Materialized view updated successfully.";
    }

    @PostMapping("/update-all")
    public String updateMaterializedView() {
        materializedViewRefreshService.refreshAllMaterializedViews();
        return "Materialized view updated successfully.";
    }
}
