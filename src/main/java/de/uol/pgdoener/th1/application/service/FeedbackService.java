package de.uol.pgdoener.th1.application.service;

import de.uol.pgdoener.th1.application.mapper.FeedbackMapper;
import de.uol.pgdoener.th1.application.dto.FeedbackDto;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Feedback;
import de.uol.pgdoener.th1.infastructure.persistence.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public UUID create(FeedbackDto request) {
        Feedback feedback = FeedbackMapper.toEntity(request);
        return feedbackRepository.save(feedback).getId();
    }

}
