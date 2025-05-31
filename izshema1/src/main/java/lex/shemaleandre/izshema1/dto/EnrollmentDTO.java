package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.UUID;

// DTO for transferring enrollment data
@Data
public class EnrollmentDTO {
    private UUID enrollmentId;
    private CourseDTO course;
    private Double progress;
    private Boolean completed;
    private UUID lastLectureViewedId;
}