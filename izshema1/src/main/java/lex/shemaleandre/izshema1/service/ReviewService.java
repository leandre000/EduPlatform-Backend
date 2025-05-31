package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.ReviewDTO;
import lex.shemaleandre.izshema1.entity.Course;
import lex.shemaleandre.izshema1.entity.Review;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.CourseRepository;
import lex.shemaleandre.izshema1.repository.EnrollmentRepository;
import lex.shemaleandre.izshema1.repository.ReviewRepository;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for managing course reviews
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // Create a review for a course (Student only)
    public ApiResponse createReview(UUID courseId, ReviewDTO reviewDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can create reviews");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        if (enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId).isEmpty()) {
            throw new IllegalStateException("Must be enrolled to review");
        }
        if (reviewRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalStateException("Review already exists for this course");
        }
        Review review = new Review();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        reviewRepository.save(review);
        updateCourseAverageRating(course);
        return new ApiResponse("Review created successfully", mapToReviewDTO(review));
    }

    // Get all reviews for a course
    public ApiResponse getReviewsByCourseId(UUID courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        List<ReviewDTO> reviews = reviewRepository.findByCourseId(courseId).stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
        return new ApiResponse("Reviews retrieved", reviews);
    }

    // Update a review (Student only)
    public ApiResponse updateReview(UUID reviewId, ReviewDTO reviewDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);
        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can update their reviews");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        if (!review.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("Not authorized to update this review");
        }
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        reviewRepository.save(review);
        updateCourseAverageRating(review.getCourse());
        return new ApiResponse("Review updated successfully", mapToReviewDTO(review));
    }

    // Delete a review (Student or Admin)
    public ApiResponse deleteReview(UUID reviewId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        if (!user.getRole().equals(User.Role.ADMIN) &&
                (!user.getRole().equals(User.Role.STUDENT) || !review.getStudent().getId().equals(user.getId()))) {
            throw new UnauthorizedException("Not authorized to delete this review");
        }
        Course course = review.getCourse();
        reviewRepository.delete(review);
        updateCourseAverageRating(course);
        return new ApiResponse("Review deleted successfully");
    }

    // Update course average rating
    private void updateCourseAverageRating(Course course) {
        List<Review> reviews = reviewRepository.findByCourseId(course.getId());
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        course.setAverageRating(averageRating);
        courseRepository.save(course);
    }

    // Map Review entity to ReviewDTO
    private ReviewDTO mapToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCourseId(review.getCourse().getId());
        dto.setStudentId(review.getStudent().getId());
        dto.setStudentName(review.getStudent().getFirstName() + " " + review.getStudent().getLastName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        return dto;
    }
}