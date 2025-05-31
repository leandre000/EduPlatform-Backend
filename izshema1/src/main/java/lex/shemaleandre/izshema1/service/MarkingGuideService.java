package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.MarkingGuideDTO;
import lex.shemaleandre.izshema1.entity.Course;
import lex.shemaleandre.izshema1.entity.MarkingGuide;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.CourseRepository;
import lex.shemaleandre.izshema1.repository.EnrollmentRepository;
import lex.shemaleandre.izshema1.repository.MarkingGuideRepository;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class MarkingGuideService {

    @Autowired
    private MarkingGuideRepository markingGuideRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public ApiResponse uploadMarkingGuide(UUID courseId, String title, MultipartFile file) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(email);

        if (instructor == null || !instructor.getRole().equals(User.Role.INSTRUCTOR)) {
            throw new UnauthorizedException("Only instructors can upload marking guides");
        }

        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new UnauthorizedException("Not authorized to upload for this course");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be null or empty");
        }

        // Save file to local storage
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get("uploads/marking_guides");
        Path filePath = uploadDir.resolve(fileName);

        try {
            Files.createDirectories(uploadDir);
            // Using Files.copy() instead of Files.write() for better file handling
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to save file: " + e.getMessage(), e);
        }

        MarkingGuide markingGuide = new MarkingGuide();
        markingGuide.setCourse(course);
        markingGuide.setTitle(title);
        markingGuide.setFileUrl(filePath.toString());
        markingGuideRepository.save(markingGuide);

        return new ApiResponse("Marking guide uploaded successfully", mapToMarkingGuideDTO(markingGuide));
    }

    public Resource downloadMarkingGuide(UUID guideId) throws MalformedURLException {
        if (guideId == null) {
            throw new IllegalArgumentException("Guide ID cannot be null");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByEmail(email);

        if (student == null || !student.getRole().equals(User.Role.STUDENT)) {
            throw new UnauthorizedException("Only students can download marking guides");
        }

        MarkingGuide markingGuide = markingGuideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Marking guide not found with ID: " + guideId));

        if (enrollmentRepository.findByStudentIdAndCourseId(student.getId(), markingGuide.getCourse().getId()).isEmpty()) {
            throw new UnauthorizedException("Must be enrolled to download");
        }

        Path filePath = Paths.get(markingGuide.getFileUrl());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("File not found or not readable: " + filePath);
        }

        return resource;
    }

    private MarkingGuideDTO mapToMarkingGuideDTO(MarkingGuide markingGuide) {
        MarkingGuideDTO dto = new MarkingGuideDTO();
        dto.setId(markingGuide.getId());
        dto.setCourseId(markingGuide.getCourse().getId());
        dto.setTitle(markingGuide.getTitle());
        dto.setFileUrl(markingGuide.getFileUrl());
        return dto;
    }
}