package de.uol.pgdoener.th1.domain.datatable.service;

import de.uol.pgdoener.th1.autoconfigure.SnapshotProperties;
import de.uol.pgdoener.th1.infastructure.persistence.entity.SchemaVersion;
import de.uol.pgdoener.th1.infastructure.persistence.repository.SchemaVersionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SchemaVersionService {

    private final SnapshotProperties snapshotProperties;

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
        schemaVersion.setSnapshotPath(snapshotProperties.getDir());
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
