package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.FeedbackApiDelegate;
import de.uol.pgdoener.th1.business.dto.FeedbackDto;
import de.uol.pgdoener.th1.business.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackController implements FeedbackApiDelegate {

    private final FeedbackService feedbackService;

    @Override
    public ResponseEntity<Long> submitFeedback(FeedbackDto request) {
        log.debug("Received Feedback {}", request);
        long id = feedbackService.create(request);
        log.debug("Feedback saved with id {}", id);
        return ResponseEntity.status(201).body(id);
    }

}
