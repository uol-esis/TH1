package de.uol.pgdoener.th1.business.mapper;

import de.uol.pgdoener.th1.data.entity.ColumnMeta;
import de.uol.pgdoener.th1.data.entity.TableMeta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableMetaMapper {

    public static List<TableMeta> toTableMetaList(List<Map<String, Object>> rows) {
        Map<String, TableMeta> tableMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            String tableName = row.get("table_name").toString();
            ColumnMeta column = toColumnMeta(row);

            TableMeta table = tableMap.computeIfAbsent(tableName, TableMetaMapper::createEmptyTable);

            table.columns().add(column);
        }

        return new ArrayList<>(tableMap.values());
    }

    //private methods//

    private static ColumnMeta toColumnMeta(Map<String, Object> row) {
        return new ColumnMeta(
                row.get("column_name").toString(),
                row.get("data_type").toString(),
                row.get("is_nullable").toString(),
                row.get("constraint_type").toString()
        );
    }

    private static TableMeta createEmptyTable(String tableName) {
        return new TableMeta(tableName, new ArrayList<>());
    }

}
