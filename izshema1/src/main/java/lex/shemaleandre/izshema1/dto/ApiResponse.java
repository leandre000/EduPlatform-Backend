package lex.shemaleandre.izshema1.dto;

import lombok.Data;

// DTO for standardized API responses
@Data
public class ApiResponse {
    private String message;
    private Object data;

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.message = message;
    }
}