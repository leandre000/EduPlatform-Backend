package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.ComplaintDTO;
import lex.shemaleandre.izshema1.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.UUID;

// Controller for complaint management
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // Create a complaint (Student only)
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse> createComplaint(@Valid @RequestBody ComplaintDTO complaintDTO) throws MessagingException {
        return new ResponseEntity<>(complaintService.createComplaint(complaintDTO), HttpStatus.CREATED);
    }

    // Get complaints (Student, Instructor, or Admin)
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> getComplaints() {
        return ResponseEntity.ok(complaintService.getComplaints());
    }

    // Respond to a complaint (Instructor or Admin)
    @PutMapping("/{complaintId}/respond")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> respondToComplaint(@PathVariable UUID complaintId,
                                                          @Valid @RequestBody ComplaintDTO complaintDTO) throws MessagingException {
        return ResponseEntity.ok(complaintService.respondToComplaint(complaintId, complaintDTO));
    }
}