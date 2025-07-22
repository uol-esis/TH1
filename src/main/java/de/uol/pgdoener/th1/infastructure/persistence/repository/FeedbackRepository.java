package de.uol.pgdoener.th1.infastructure.persistence.repository;

import de.uol.pgdoener.th1.infastructure.persistence.entity.Feedback;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackRepository
        extends CrudRepository<Feedback, UUID>,
        JpaSpecificationExecutor<Feedback> {
}
