package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.AssignmentDTO;
import lex.shemaleandre.izshema1.dto.SubmissionDTO;
import lex.shemaleandre.izshema1.entity.*;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for managing assignments and submissions
@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    private EnrollmentRepository enrollmentRepository;

    // Create an assignment (Instructor only)
    public ApiResponse createAssignment(UUID courseId, AssignmentDTO assignmentDTO) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(email);
        if (instructor == null || !instructor.getRole().equals(User.Role.INSTRUCTOR)) {
            throw new UnauthorizedException("Only instructors can create assignments");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new UnauthorizedException("Not authorized to create assignments for this course");
        }
        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setDescription(assignmentDTO.getDescription());
        assignment.setDueDate(assignmentDTO.getDueDate());
        assignment.setMaxMarks(assignmentDTO.getMaxMarks());
        assignmentRepository.save(assignment);

        // Notify enrolled students
        List<User> students = enrollmentRepository.findByCourseId(courseId).stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());
        for (User student : students) {
            emailService.sendEmail(student.getEmail(), "New Assignment: " + assignment.getTitle(),
                    "A new assignment has been posted for " + course.getTitle() + ". Due date: " + assignment.getDueDate());
        }

        return new ApiResponse("Assignment created successfully", mapToAssignmentDTO(assignment));
    }

    // Submit an assignment (Student only)
    public ApiResponse submitAssignment(UUID assignmentId, SubmissionDTO submissionDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can submit assignments");
        }
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (enrollmentRepository.findByStudentIdAndCourseId(student.getId(), assignment.getCourse().getId()) == null) {
            throw new UnauthorizedException("Must be enrolled to submit");
        }
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmissionText(submissionDTO.getSubmissionText());
        submission.setFileUrl(submissionDTO.getFileUrl());
        submission.setStatus(Submission.SubmissionStatus.SUBMITTED);
        submissionRepository.save(submission);
        return new ApiResponse("Assignment submitted successfully", mapToSubmissionDTO(submission));
    }

    // View submissions for an assignment (Instructor only)
    public ApiResponse getSubmissions(UUID assignmentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(email);
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (!instructor.getRole().equals(User.Role.INSTRUCTOR) ||
                !assignment.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new UnauthorizedException("Not authorized to view submissions");
        }
        List<SubmissionDTO> submissions = submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(this::mapToSubmissionDTO)
                .collect(Collectors.toList());
        return new ApiResponse("Submissions retrieved", submissions);
    }

    // Mark an assignment submission (Instructor only)
    public ApiResponse markSubmission(UUID assignmentId, UUID submissionId, SubmissionDTO submissionDTO) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(email);
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        if (!instructor.getRole().equals(User.Role.INSTRUCTOR) ||
                !assignment.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new UnauthorizedException("Not authorized to mark submissions");
        }
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submission.setMarksObtained(submissionDTO.getMarksObtained());
        submission.setFeedback(submissionDTO.getFeedback());
        submission.setStatus(Submission.SubmissionStatus.GRADED);
        submissionRepository.save(submission);

        // Notify student
        emailService.sendEmail(submission.getStudent().getEmail(), "Assignment Graded: " + assignment.getTitle(),
                "Your submission has been graded. Marks: " + submission.getMarksObtained() + ". Feedback: " + submission.getFeedback());

        return new ApiResponse("Assignment marked successfully", mapToSubmissionDTO(submission));
    }

    // Map Assignment entity to AssignmentDTO
    private AssignmentDTO mapToAssignmentDTO(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setCourseId(assignment.getCourse().getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDueDate(assignment.getDueDate());
        dto.setMaxMarks(assignment.getMaxMarks());
        return dto;
    }

    // Map Submission entity to SubmissionDTO
    private SubmissionDTO mapToSubmissionDTO(Submission submission) {
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(submission.getId());
        dto.setAssignmentId(submission.getAssignment().getId());
        dto.setStudentId(submission.getStudent().getId());
        dto.setStudentName(submission.getStudent().getFirstName() + " " + submission.getStudent().getLastName());
        dto.setSubmissionText(submission.getSubmissionText());
        dto.setFileUrl(submission.getFileUrl());
        dto.setStatus(submission.getStatus().name());
        dto.setMarksObtained(submission.getMarksObtained());
        dto.setFeedback(submission.getFeedback());
        dto.setSubmissionDate(submission.getSubmissionDate());
        return dto;
    }
}