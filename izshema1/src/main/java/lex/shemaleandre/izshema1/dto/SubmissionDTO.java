package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO for transferring submission data
@Data
public class SubmissionDTO {
    private UUID id;
    private UUID assignmentId;
    private UUID studentId;
    private String studentName;
    private String submissionText;
    private String fileUrl;
    private String status;
    private Integer marksObtained;
    private String feedback;
    private LocalDateTime submissionDate;
}