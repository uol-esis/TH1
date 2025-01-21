package de.uol.pgdoener.th1.data.repository;

import de.uol.pgdoener.th1.data.entity.TableStructure;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableStructureRepository extends
        CrudRepository<TableStructure, Long>,
        JpaSpecificationExecutor<TableStructure> {
}
