package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.AssignmentDTO;
import lex.shemaleandre.izshema1.dto.SubmissionDTO;
import lex.shemaleandre.izshema1.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.UUID;

// Controller for assignment management
@RestController
@RequestMapping("/api")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // Create an assignment (Instructor only)
    @PostMapping("/courses/{courseId}/assignments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse> createAssignment(@PathVariable UUID courseId, @Valid @RequestBody AssignmentDTO assignmentDTO) throws MessagingException {
        return new ResponseEntity<>(assignmentService.createAssignment(courseId, assignmentDTO), HttpStatus.CREATED);
    }

    // Submit an assignment (Student only)
    @PostMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> submitAssignment(@PathVariable UUID assignmentId, @Valid @RequestBody SubmissionDTO submissionDTO) {
        return new ResponseEntity<>(assignmentService.submitAssignment(assignmentId, submissionDTO), HttpStatus.CREATED);
    }

    // View submissions for an assignment (Instructor only)
    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse> getSubmissions(@PathVariable UUID assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissions(assignmentId));
    }

    // Mark a submission (Instructor only)
    @PutMapping("/assignments/{assignmentId}/submissions/{submissionId}/mark")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse> markSubmission(@PathVariable UUID assignmentId, @PathVariable UUID submissionId,
                                                      @Valid @RequestBody SubmissionDTO submissionDTO) throws MessagingException {
        return ResponseEntity.ok(assignmentService.markSubmission(assignmentId, submissionId, submissionDTO));
    }
}