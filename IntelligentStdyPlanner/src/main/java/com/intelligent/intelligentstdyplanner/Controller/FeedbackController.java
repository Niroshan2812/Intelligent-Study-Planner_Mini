package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.DTO.FeedbackRequest;
import com.intelligent.intelligentstdyplanner.Service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{sessionId}")
    public ResponseEntity<String> submitFeedback(@PathVariable Long sessionId, @RequestBody FeedbackRequest request) {
        try {
            feedbackService.submitFeedback(sessionId, request.getActualDurationMinutes(),
                    request.getComprehensionRating());
            return ResponseEntity.ok("Feedback submitted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error submitting feedback: " + e.getMessage());
        }
    }
}
