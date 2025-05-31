package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

// DTO for transferring course data
@Data
public class CourseDTO {
    private UUID id;
    private String title;
    private String description;
    private Double price;
    private String prerequisites;
    private UUID instructorId;
    private String instructorName;
    private Integer enrollmentCount;
    private Double averageRating;
    private List<SectionDTO> sections;
}