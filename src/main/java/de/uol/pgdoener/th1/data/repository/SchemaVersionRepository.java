package de.uol.pgdoener.th1.data.repository;

import de.uol.pgdoener.th1.data.entity.SchemaVersion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SchemaVersionRepository extends CrudRepository<SchemaVersion, UUID>, JpaSpecificationExecutor<SchemaVersion> {
    List<SchemaVersion> findByTableNameOrderByVersionDesc(String tableName);
}
