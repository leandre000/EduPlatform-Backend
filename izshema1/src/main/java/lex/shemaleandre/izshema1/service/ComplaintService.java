package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.ComplaintDTO;
import lex.shemaleandre.izshema1.entity.Complaint;
import lex.shemaleandre.izshema1.entity.Course;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.ComplaintRepository;
import lex.shemaleandre.izshema1.repository.CourseRepository;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for managing complaints
@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EmailService emailService;

    // Create a complaint (Student only)
    public ApiResponse createComplaint(ComplaintDTO complaintDTO) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can submit complaints");
        }
        Course course = complaintDTO.getCourseId() != null ?
                courseRepository.findById(complaintDTO.getCourseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found")) : null;
        Complaint complaint = new Complaint();
        complaint.setStudent(student);
        complaint.setCourse(course);
        complaint.setSubject(complaintDTO.getSubject());
        complaint.setDescription(complaintDTO.getDescription());
        complaint.setStatus(Complaint.ComplaintStatus.PENDING);
        complaintRepository.save(complaint);

        // Notify instructor or admin
        String recipientEmail = course != null ? course.getInstructor().getEmail() : "admin@example.com";
        emailService.sendEmail(recipientEmail, "New Complaint: " + complaint.getSubject(),
                "A new complaint has been submitted: " + complaint.getDescription());

        return new ApiResponse("Complaint submitted successfully", mapToComplaintDTO(complaint));
    }

    // Get complaints for the current user (Student) or instructor/admin
    public ApiResponse getComplaints() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        List<Complaint> complaints;
        if (user.getRole().equals(User.Role.STUDENT)) {
            complaints = complaintRepository.findByStudentId(user.getId());
        } else if (user.getRole().equals(User.Role.INSTRUCTOR)) {
            complaints = complaintRepository.findByCourseInstructorId(user.getId());
        } else { // ADMIN
            complaints = complaintRepository.findAll();
        }
        List<ComplaintDTO> complaintDTOs = complaints.stream()
                .map(this::mapToComplaintDTO)
                .collect(Collectors.toList());
        return new ApiResponse("Complaints retrieved", complaintDTOs);
    }

    // Respond to a complaint (Instructor or Admin)
    public ApiResponse respondToComplaint(UUID complaintId, ComplaintDTO complaintDTO) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        if (!user.getRole().equals(User.Role.ADMIN) &&
                (!user.getRole().equals(User.Role.INSTRUCTOR) ||
                        (complaint.getCourse() != null && !complaint.getCourse().getInstructor().getId().equals(user.getId())))) {
            throw new UnauthorizedException("Not authorized to respond to this complaint");
        }
        complaint.setResponseText(complaintDTO.getResponseText());
        complaint.setStatus(Complaint.ComplaintStatus.valueOf(complaintDTO.getStatus()));
        complaintRepository.save(complaint);

        // Notify student
        emailService.sendEmail(complaint.getStudent().getEmail(), "Complaint Response: " + complaint.getSubject(),
                "Your complaint has been responded to: " + complaint.getResponseText());

        return new ApiResponse("Complaint updated and responded to");
    }

    // Map Complaint entity to ComplaintDTO
    private ComplaintDTO mapToComplaintDTO(Complaint complaint) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(complaint.getId());
        dto.setStudentId(complaint.getStudent().getId());
        dto.setStudentName(complaint.getStudent().getFirstName() + " " + complaint.getStudent().getLastName());
        dto.setCourseId(complaint.getCourse() != null ? complaint.getCourse().getId() : null);
        dto.setCourseTitle(complaint.getCourse() != null ? complaint.getCourse().getTitle() : null);
        dto.setSubject(complaint.getSubject());
        dto.setDescription(complaint.getDescription());
        dto.setStatus(complaint.getStatus().name());
        dto.setResponseText(complaint.getResponseText());
        dto.setSubmissionDate(complaint.getSubmissionDate());
        return dto;
    }
}