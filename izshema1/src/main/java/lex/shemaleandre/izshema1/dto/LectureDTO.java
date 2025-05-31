package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.UUID;

// DTO for transferring lecture data
@Data
public class LectureDTO {
    private UUID id;
    private String title;
    private String type;
    private String contentUrl;
    private String contentText;
}