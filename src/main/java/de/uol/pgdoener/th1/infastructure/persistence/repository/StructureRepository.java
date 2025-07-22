package de.uol.pgdoener.th1.infastructure.persistence.repository;

import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureRepository extends
        CrudRepository<Structure, Long>,
        JpaSpecificationExecutor<Structure> {
    List<Structure> findByTableStructureId(Long tableStructureId);

    void deleteByTableStructureId(Long tableStructureId);
}
