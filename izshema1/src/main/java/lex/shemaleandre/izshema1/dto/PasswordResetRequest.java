package lex.shemaleandre.izshema1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO for password reset request
@Data
public class PasswordResetRequest {
    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    private String newPassword;
}