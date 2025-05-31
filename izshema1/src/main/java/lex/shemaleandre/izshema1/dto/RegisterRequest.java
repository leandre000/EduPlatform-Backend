package lex.shemaleandre.izshema1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO for registration request
@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String bio;
}