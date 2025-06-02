package lex.shemaleandre.izshema1.dto;

import lombok.Data;
import java.util.UUID;

// DTO for transferring user data
@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private String bio;
}