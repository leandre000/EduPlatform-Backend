package lex.shemaleandre.izshema1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO for password change request
@Data
public class PasswordChangeRequest {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;
}