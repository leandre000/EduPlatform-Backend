package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO for transferring assignment data
@Data
public class AssignmentDTO {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer maxMarks;
}