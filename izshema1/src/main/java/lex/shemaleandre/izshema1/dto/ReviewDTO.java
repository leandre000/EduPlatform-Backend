package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.UUID;

// DTO for transferring review data
@Data
public class ReviewDTO {
    private UUID id;
    private UUID courseId;
    private UUID studentId;
    private String studentName;
    private Integer rating;
    private String comment;
}