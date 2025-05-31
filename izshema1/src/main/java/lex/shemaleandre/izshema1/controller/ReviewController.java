package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.ReviewDTO;
import lex.shemaleandre.izshema1.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

// Controller for review management
@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Get reviews for a course (public)
    @GetMapping
    public ResponseEntity<ApiResponse> getReviews(@PathVariable UUID courseId) {
        return ResponseEntity.ok(reviewService.getReviewsByCourseId(courseId));
    }

    // Create a review (Student only)
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> createReview(@PathVariable UUID courseId, @Valid @RequestBody ReviewDTO reviewDTO) {
        return new ResponseEntity<>(reviewService.createReview(courseId, reviewDTO), HttpStatus.CREATED);
    }

    // Update a review (Student only)
    @PutMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> updateReview(@PathVariable UUID courseId, @Valid @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(courseId, reviewDTO));
    }

    // Delete a review (Student or Admin)
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable UUID reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }
}