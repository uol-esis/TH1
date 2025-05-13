package de.uol.pgdoener.th1.data.repository;

import de.uol.pgdoener.th1.data.entity.Feedback;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository
        extends CrudRepository<Feedback, Long>,
        JpaSpecificationExecutor<Feedback> {
}
