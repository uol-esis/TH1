package de.uol.pgdoener.th1.business.service.datatable.service;

import de.uol.pgdoener.th1.business.service.SchemaVersionService;
import de.uol.pgdoener.th1.business.service.datatable.helper.SqlHeaderBuilder;
import de.uol.pgdoener.th1.business.service.datatable.helper.SqlQueryBuilder;
import de.uol.pgdoener.th1.business.service.datatable.helper.SqlValidator;
import de.uol.pgdoener.th1.business.service.datatable.helper.SqlValueBuilder;
import de.uol.pgdoener.th1.data.entity.SchemaVersion;
import de.uol.pgdoener.th1.data.repository.DynamicTableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final int BATCH_SIZE = 1000;

    final DynamicTableRepository dynamicTableRepository;

    final SqlHeaderBuilder headerBuilder;
    final SqlValidator sqlValidator;
    final SqlQueryBuilder queryBuilder;
    final SqlValueBuilder sqlValueBuilder;

    final SchemaVersionService schemaVersionService;

    @Transactional
    public void createDatabaseTableWithValues(String tableName, String[][] matrix) {
        Map<String, String> columns = headerBuilder.build(matrix);

        sqlValidator.validateTableName(tableName);
        sqlValidator.validateHeaders(columns);

        String sql = queryBuilder.buildCreateTableQuery(tableName, columns);
        dynamicTableRepository.executeRawSql(sql);

        insertValuesIntoTable(tableName, columns, matrix);

        schemaVersionService.saveVersion(tableName, "CREATED", sql, matrix);
    }

    @Transactional
    public void extendDatabaseTableWithValues(String tableName, String[][] matrix) {
        Map<String, String> columns = headerBuilder.build(matrix);

        sqlValidator.validateTableName(tableName);
        sqlValidator.validateHeaders(columns);

        insertValuesIntoTable(tableName, columns, matrix);

        schemaVersionService.saveVersion(tableName, "EXTEND", "", matrix);
    }

    @Transactional
    public void replaceDatabaseTableWithValues(String tableName, String[][] matrix) {
        Map<String, String> columns = headerBuilder.build(matrix);

        sqlValidator.validateTableName(tableName);
        sqlValidator.validateHeaders(columns);

        String deleteSql = "DELETE FROM " + tableName;
        dynamicTableRepository.executeRawSql(deleteSql);

        insertValuesIntoTable(tableName, columns, matrix);

        schemaVersionService.saveVersion(tableName, "REPLACE", deleteSql, matrix);
    }

    public List<Map<String, Object>> getTableMetadata(String tableName) {
        return dynamicTableRepository.getTableMetadata();
    }

    public List<String> getTableNames() {
        return dynamicTableRepository.getAllTableNames();
    }

    @Transactional
    public void transformDatabaseTableWithValues(String tableName, String[][] matrix) {
        //Map<String, String> columns = headerBuilder.build(matrix);

        //sqlValidator.validateTableName(tableName);
        //sqlValidator.validateHeaders(columns);

        //Set<String> existingColumns = dynamicTableRepository.getExistingColumnNames(tableName);

        //List<String> alterStatements = queryBuilder.buildAlterTableQueries(tableName, existingColumns, columns);
        //for (String alterSql : alterStatements) {
        //    dynamicTableRepository.executeRawSql(alterSql);
        //}

        //insertValuesIntoTable(tableName, columns, matrix);
    }

    public void rollbackToVersion(String tableName, int version) throws IOException {

        SchemaVersion versionMeta = schemaVersionService.getVersion(tableName, version);

        dynamicTableRepository.executeRawSql("DROP TABLE IF EXISTS " + tableName);
        dynamicTableRepository.executeRawSql(versionMeta.getChangeSql());

        List<String[]> matrix = loadMatrixFromCsv(versionMeta.getSnapshotPath());
        Map<String, String> columns = headerBuilder.build(matrix.toArray(new String[0][0]));
        insertValuesIntoTable(tableName, columns, matrix.toArray(new String[0][0]));
    }

    private List<String[]> loadMatrixFromCsv(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        return lines.stream()
                .map(line -> line.split(","))
                .toList();
    }

    private void insertValuesIntoTable(String tableName, Map<String, String> columns, String[][] matrix) {
        /// TODO: auslagern ?
        int maxParams = 65535;
        int columnsCount = columns.size();
        int batchSize = Math.min(BATCH_SIZE, maxParams / columnsCount);

        String insertSql = queryBuilder.buildInsertQuery(tableName, columns);

        List<Object[]> values = sqlValueBuilder.build(columns, matrix);

        for (int i = 0; i < values.size(); i += batchSize) {
            int end = Math.min(i + batchSize, values.size());
            List<Object[]> batch = values.subList(i, end);
            dynamicTableRepository.executeRawSqlWithBatch(insertSql, batch);
        }
    }

}
