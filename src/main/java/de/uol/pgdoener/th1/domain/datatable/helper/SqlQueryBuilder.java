package de.uol.pgdoener.th1.domain.datatable.helper;

import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import de.uol.pgdoener.th1.domain.datatable.model.SqlType;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.jooq.impl.DSL.table;

@Component
@RequiredArgsConstructor
public class SqlQueryBuilder {

    private final DSLContext dsl;

    public void buildDataTable(String tableName, List<SqlColumn> columns) {
        Name tbName = DSL.name(tableName);
        CreateTableElementListStep step = dsl.createTableIfNotExists(tbName);

        for (SqlColumn col : columns) {
            Name colName = DSL.name(col.getName());
            DataType<?> type = toSqlDatatype(col.getType());
            step = step.column(colName, type);
        }

        step.execute();
    }

    public void insertValuesIntoTable(String tableName, List<SqlColumn> columns, List<Object[]> values) {
        final int MAX_PARAMS = 65535;
        final int BATCH_SIZE = Math.min(1000, MAX_PARAMS / columns.size());

        Table<?> table = table(DSL.name(tableName));
        Field<?>[] fields = new Field<?>[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            fields[i] = DSL.field(DSL.name(columns.get(i).getName()));
        }

        // Batching
        for (int i = 0; i < values.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, values.size());
            List<Object[]> batch = values.subList(i, end);

            Query[] inserts = batch.stream()
                    .map(row -> DSL.insertInto(table)
                            .columns(fields)
                            .values(row))
                    .toArray(Query[]::new);

            dsl.batch(inserts).execute();
        }
    }

    public void deleteTable(String tableName) {
        Table<?> table = table(DSL.name(tableName));
        dsl.deleteFrom(table)
                .execute();
    }

    private DataType<?> toSqlDatatype(SqlType columnType) {
        return switch (columnType) {
            case TEXT -> SQLDataType.CLOB;
            case SERIAL_PRIMARY_KEY -> SQLDataType.BIGINT;
            case INTEGER -> SQLDataType.INTEGER;
            case NUMERIC -> SQLDataType.NUMERIC;
            case BOOLEAN -> SQLDataType.BOOLEAN;
            case DATE -> SQLDataType.DATE;
            case TIMESTAMP -> SQLDataType.TIMESTAMP;
            case UUID -> SQLDataType.UUID;
            case UNDEFINED -> SQLDataType.VARCHAR;
        };
    }

}
