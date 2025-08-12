package de.uol.pgdoener.th1.infastructure.persistence.repository;

import de.uol.pgdoener.th1.infastructure.persistence.entity.TableStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableStructureRepository extends
        CrudRepository<TableStructure, Long>,
        JpaRepository<TableStructure, Long>,
        JpaSpecificationExecutor<TableStructure> {
    boolean existsById(Long id);
}
