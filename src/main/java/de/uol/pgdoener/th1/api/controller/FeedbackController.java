package de.uol.pgdoener.th1.api.controller;

import de.uol.pgdoener.th1.api.FeedbackApiDelegate;
import de.uol.pgdoener.th1.application.service.FeedbackService;
import de.uol.pgdoener.th1.application.dto.FeedbackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackController implements FeedbackApiDelegate {

    private final FeedbackService feedbackService;

    @Override
    @PreAuthorize("hasAuthority('write:feedback')")
    public ResponseEntity<UUID> submitFeedback(FeedbackDto request) {
        log.debug("Received Feedback {}", request);
        UUID id = feedbackService.create(request);
        log.debug("Feedback saved with id {}", id);
        return ResponseEntity.status(201).body(id);
    }

}
