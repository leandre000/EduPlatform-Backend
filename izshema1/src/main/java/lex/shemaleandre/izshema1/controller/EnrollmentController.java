package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

// Controller for enrollment management
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // Enroll in a course (Student only)
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> enroll(@RequestBody Map<String, UUID> request) {
        return new ResponseEntity<>(enrollmentService.enroll(request.get("courseId")), HttpStatus.CREATED);
    }

    // Get my enrollments (Student only)
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> getMyEnrollments() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    // Mark lecture as completed (Student only)
    @PostMapping("/{enrollmentId}/lectures/{lectureId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> markLectureCompleted(@PathVariable UUID enrollmentId, @PathVariable UUID lectureId) {
        return ResponseEntity.ok(enrollmentService.markLectureCompleted(enrollmentId, lectureId));
    }
}