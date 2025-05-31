package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.LoginRequest;
import lex.shemaleandre.izshema1.dto.PasswordResetRequest;
import lex.shemaleandre.izshema1.dto.RegisterRequest;
import lex.shemaleandre.izshema1.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

// Controller for authentication endpoints
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register a student
    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse> registerStudent(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        return new ResponseEntity<>(authService.registerStudent(request), HttpStatus.CREATED);
    }

    // Register an instructor
    @PostMapping("/register/instructor")
    public ResponseEntity<ApiResponse> registerInstructor(@Valid @RequestBody RegisterRequest request) throws MessagingException {
        return new ResponseEntity<>(authService.registerInstructor(request), HttpStatus.CREATED);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Request password reset
    @PostMapping("/forgot-password/request")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody String email) throws MessagingException {
        return ResponseEntity.ok(authService.requestPasswordReset(email));
    }

    // Reset password
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}