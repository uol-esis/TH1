package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.data.entity.SchemaVersion;
import de.uol.pgdoener.th1.data.repository.SchemaVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SchemaVersionService {

    @Value("${snapshot.dir}")
    private String snapshotPath;

    private final SnapshotService snapshotService;
    private final SchemaVersionRepository schemaVersionRepository;

    @Transactional
    public void saveVersion(String tableName, String changeType, String sql, String[][] matrix) {
        int version = schemaVersionRepository.findByTableNameOrderByVersionDesc(tableName)
                .stream()
                .findFirst()
                .map(SchemaVersion::getVersion)
                .orElse(0) + 1;

        try {
            snapshotService.saveAsCsv(tableName, version, matrix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SchemaVersion schemaVersion = new SchemaVersion();
        schemaVersion.setTableName(tableName);
        schemaVersion.setVersion(version);
        schemaVersion.setChangeType(changeType);
        schemaVersion.setChangeSql(sql);
        schemaVersion.setSnapshotPath(snapshotPath);
        schemaVersion.setChangedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        schemaVersionRepository.save(schemaVersion);
    }

    public SchemaVersion getVersion(String tableName, int version) {
        return schemaVersionRepository.findByTableNameOrderByVersionDesc(tableName)
                .stream()
                .filter(v -> v.getVersion() == version)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));
    }

    //public void save(String tableName, String changeType, String sql) {
    //    SchemaVersion schemaVersion = new SchemaVersion(
    //            null, tableName, changeType, sql, new Timestamp(System.currentTimeMillis())
    //    );
    //    schemaVersionRepository.save(schemaVersion);
    //}
}
