package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO for transferring complaint data
@Data
public class ComplaintDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID courseId;
    private String courseTitle;
    private String subject;
    private String description;
    private String status;
    private String responseText;
    private LocalDateTime submissionDate;
}