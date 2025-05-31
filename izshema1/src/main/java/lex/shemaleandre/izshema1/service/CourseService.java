package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.CourseDTO;
import lex.shemaleandre.izshema1.entity.Course;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.CourseRepository;
import lex.shemaleandre.izshema1.repository.EnrollmentRepository;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for course management
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // Create a new course (Instructor only)
    public ApiResponse createCourse(CourseDTO courseDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(email);
        if (instructor == null || !instructor.getRole().equals(User.Role.INSTRUCTOR)) {
            throw new UnauthorizedException("Only instructors can create courses");
        }
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setPrice(courseDTO.getPrice());
        course.setPrerequisites(courseDTO.getPrerequisites());
        course.setInstructor(instructor);
        courseRepository.save(course);
        return new ApiResponse("Course created successfully", mapToCourseDTO(course));
    }

    // Get all courses with pagination
    public ApiResponse getAllCourses(int page, int size) {
        Page<Course> courses = courseRepository.findAll(PageRequest.of(page, size));
        List<CourseDTO> courseDTOs = courses.stream()
                .map(this::mapToCourseDTO)
                .collect(Collectors.toList());
        return new ApiResponse("Courses retrieved", courseDTOs);
    }

    // Get course by ID
    public ApiResponse getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return new ApiResponse("Course retrieved", mapToCourseDTO(course));
    }

    // Update course (Instructor or Admin)
    public ApiResponse updateCourse(UUID courseId, CourseDTO courseDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!user.getRole().equals(User.Role.ADMIN) &&
                (!user.getRole().equals(User.Role.INSTRUCTOR) || !course.getInstructor().getId().equals(user.getId()))) {
            throw new UnauthorizedException("Not authorized to update this course");
        }
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setPrice(courseDTO.getPrice());
        course.setPrerequisites(courseDTO.getPrerequisites());
        courseRepository.save(course);
        return new ApiResponse("Course updated successfully");
    }

    // Delete course (Instructor or Admin)
    public ApiResponse deleteCourse(UUID courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!user.getRole().equals(User.Role.ADMIN) &&
                (!user.getRole().equals(User.Role.INSTRUCTOR) || !course.getInstructor().getId().equals(user.getId()))) {
            throw new UnauthorizedException("Not authorized to delete this course");
        }
        if (!enrollmentRepository.findByCourseId(courseId).isEmpty()) {
            throw new IllegalStateException("Cannot delete course with active enrollments");
        }
        courseRepository.delete(course);
        return new ApiResponse("Course deleted successfully");
    }

    // Map Course entity to CourseDTO
    public CourseDTO mapToCourseDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());
        dto.setPrerequisites(course.getPrerequisites());
        dto.setInstructorId(course.getInstructor().getId());
        dto.setInstructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        dto.setEnrollmentCount(course.getEnrollmentCount());
        dto.setAverageRating(course.getAverageRating());
        return dto;
    }
}