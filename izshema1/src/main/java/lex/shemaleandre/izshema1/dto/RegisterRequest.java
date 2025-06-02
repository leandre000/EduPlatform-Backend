package lex.shemaleandre.izshema1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO for registration request
@Data
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String bio;
}