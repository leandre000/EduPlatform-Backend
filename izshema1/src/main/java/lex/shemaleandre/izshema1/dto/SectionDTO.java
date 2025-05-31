package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

// DTO for transferring section data
@Data
public class SectionDTO {
    private UUID id;
    private String title;
    private List<LectureDTO> lectures;
}