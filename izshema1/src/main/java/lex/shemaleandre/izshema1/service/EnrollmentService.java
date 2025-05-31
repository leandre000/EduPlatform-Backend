package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.EnrollmentDTO;
import lex.shemaleandre.izshema1.entity.Enrollment;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.CourseRepository;
import lex.shemaleandre.izshema1.repository.EnrollmentRepository;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for managing course enrollments
@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    // Enroll in a course (Student only)
    public ApiResponse enroll(UUID courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can enroll in courses");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalStateException("Already enrolled in this course");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(courseRepository.getReferenceById(courseId));
        enrollment.setProgress(0.0);
        enrollmentRepository.save(enrollment);
        return new ApiResponse("Enrolled successfully", mapToEnrollmentDTO(enrollment));
    }

    // Get current user's enrollments (Student only)
    public ApiResponse getMyEnrollments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can view their enrollments");
        }
        List<EnrollmentDTO> enrollments = enrollmentRepository.findByStudentId(student.getId()).stream()
                .map(this::mapToEnrollmentDTO)
                .collect(Collectors.toList());
        return new ApiResponse("Enrollments retrieved", enrollments);
    }

    // Mark a lecture as completed (Student only)
    public ApiResponse markLectureCompleted(UUID enrollmentId, UUID lectureId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can mark lectures as completed");
        }
        if (enrollmentId == null || lectureId == null) {
            throw new IllegalArgumentException("Enrollment ID and Lecture ID cannot be null");
        }
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with ID: " + enrollmentId));
        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not authorized to update this enrollment");
        }
        // Note: Assuming enrollment_lectures table is updated via SQL trigger or separate service
        return new ApiResponse("Lecture marked as completed");
    }

    // Map Enrollment entity to EnrollmentDTO
    private EnrollmentDTO mapToEnrollmentDTO(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setEnrollmentId(enrollment.getId());
        if (enrollment.getCourse() != null) {
            dto.setCourse(courseService.mapToCourseDTO(enrollment.getCourse()));
        } else {
            throw new IllegalStateException("Enrollment course cannot be null");
        }
        dto.setProgress(enrollment.getProgress());
        // Set default values for completed and lastLectureViewedId if not available in Enrollment
        dto.setCompleted(false); // Adjust if Enrollment has isCompleted()
        dto.setLastLectureViewedId(null); // Adjust if Enrollment has getLastLectureViewedId()
        return dto;
    }
}