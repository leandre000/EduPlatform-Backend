package lex.shemaleandre.izshema1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// Enrollment entity representing a student's enrollment in a course
@Data
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    @Column(name = "progress_percentage")
    private Double progress = 0.0;

    @Column(name = "completed")
    private Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "last_lecture_viewed_id")
    private Lecture lastLectureViewed;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}