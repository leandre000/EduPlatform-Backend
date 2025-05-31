package lex.shemaleandre.izshema1.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// Submission entity representing a student's assignment submission
@Data
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "submission_text")
    private String submissionText;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubmissionStatus status;

    @Column(name = "marks_obtained")
    private Integer marksObtained;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate = LocalDateTime.now();

    public enum SubmissionStatus {
        SUBMITTED, GRADED
    }
}