package lex.shemaleandre.izshema1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// MarkingGuide entity representing a marking guide for a course
@Data
@Entity
@Table(name = "marking_guides")
public class MarkingGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}