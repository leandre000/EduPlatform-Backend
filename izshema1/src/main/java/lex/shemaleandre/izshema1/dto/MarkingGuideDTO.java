package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.UUID;

// DTO for transferring marking guide data
@Data
public class MarkingGuideDTO {
    private UUID id;
    private UUID courseId;
    private String title;
    private String fileUrl;
}