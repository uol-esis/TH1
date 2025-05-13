package de.uol.pgdoener.th1.business.service;

import de.uol.pgdoener.th1.business.dto.FeedbackDto;
import de.uol.pgdoener.th1.business.mapper.FeedbackMapper;
import de.uol.pgdoener.th1.data.entity.Feedback;
import de.uol.pgdoener.th1.data.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public long create(FeedbackDto request) {
        Feedback feedback = FeedbackMapper.toEntity(request);
        return feedbackRepository.save(feedback).getId();
    }

}
