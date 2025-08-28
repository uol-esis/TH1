package de.uol.pgdoener.th1.domain.datatable.service;

import de.uol.pgdoener.th1.domain.datatable.helper.SqlHeaderBuilder;
import de.uol.pgdoener.th1.domain.datatable.helper.SqlQueryBuilder;
import de.uol.pgdoener.th1.domain.datatable.helper.SqlValidator;
import de.uol.pgdoener.th1.domain.datatable.helper.SqlValueBuilder;
import de.uol.pgdoener.th1.domain.datatable.model.SqlColumn;
import de.uol.pgdoener.th1.infastructure.persistence.repository.DynamicTableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    final DynamicTableRepository dynamicTableRepository;

    final SqlHeaderBuilder headerBuilder;
    final SqlValidator sqlValidator;
    final SqlQueryBuilder queryBuilder;
    final SqlValueBuilder sqlValueBuilder;

    final SchemaVersionService schemaVersionService;

    @Transactional
    public void createDatabaseTableWithValues(String tableName, String[][] matrix) {
        List<SqlColumn> header = headerBuilder.build(matrix);

        sqlValidator.validateTableName(tableName);
        sqlValidator.validateHeaders(header);

        List<Object[]> values = sqlValueBuilder.build(header, matrix);
        String sql = queryBuilder.buildCreateTableQuery(tableName, header);

        dynamicTableRepository.executeRawSql(sql);
        insertValuesIntoTable(tableName, header, values);

        schemaVersionService.saveVersion(tableName, "CREATED", sql, matrix);
    }

    @Transactional
    public void extendDatabaseTableWithValues(String tableName, String[][] matrix) {
        List<SqlColumn> header = headerBuilder.build(matrix);

        sqlValidator.validateTableName(tableName);
        sqlValidator.validateHeaders(header);

        List<Object[]> values = sqlValueBuilder.build(header, matrix);
        insertValuesIntoTable(tableName, header, values);

        schemaVersionService.saveVersion(tableName, "EXTEND", "", matrix);
    }

    @Transactional
    public void replaceDatabaseTableWithValues(String tableName, String[][] matrix) {
        List<SqlColumn> columns = headerBuilder.build(matrix);

        sqlValidator.validateTableName(tableName);
        sqlValidator.validateHeaders(columns);

        String deleteSql = "DELETE FROM " + tableName;
        List<Object[]> values = sqlValueBuilder.build(columns, matrix);

        dynamicTableRepository.executeRawSql(deleteSql);
        insertValuesIntoTable(tableName, columns, values);

        schemaVersionService.saveVersion(tableName, "REPLACE", deleteSql, matrix);
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

//    public void rollbackToVersion(String tableName, int version) throws IOException {
//
//        SchemaVersion versionMeta = schemaVersionService.getVersion(tableName, version);
//
//        dynamicTableRepository.executeRawSql("DROP TABLE IF EXISTS " + tableName);
//        dynamicTableRepository.executeRawSql(versionMeta.getChangeSql());
//
//        List<String[]> matrix = loadMatrixFromCsv(versionMeta.getSnapshotPath());
//        Map<String, String> columns = headerBuilder.build(matrix.toArray(new String[0][0]));
//        insertValuesIntoTable(tableName, columns, matrix.toArray(new String[0][0]));
//    }

    private List<String[]> loadMatrixFromCsv(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        return lines.stream()
                .map(line -> line.split(","))
                .toList();
    }

    private void insertValuesIntoTable(String tableName, List<SqlColumn> columns, List<Object[]> values) {
        /// TODO: auslagern ?
        int maxParams = 65535;
        int columnsCount = columns.size();
        int BATCH_SIZE = 1000;
        int batchSize = Math.min(BATCH_SIZE, maxParams / columnsCount);

        String insertSql = queryBuilder.buildInsertQuery(tableName, columns, values);

        for (int i = 0; i < values.size(); i += batchSize) {
            int end = Math.min(i + batchSize, values.size());
            List<Object[]> batch = values.subList(i, end);
            dynamicTableRepository.executeRawSqlWithBatch(insertSql, batch);
        }
    }

}
