package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.PasswordChangeRequest;
import lex.shemaleandre.izshema1.dto.UserDTO;
import lex.shemaleandre.izshema1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

// Controller for user profile management to perform certain operations
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Get current user's profile
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    // Update current user's profile
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> updateMyProfile(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateMyProfile(userDTO));
    }

    // Change current user's password
    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse> changeMyPassword(@Valid @RequestBody PasswordChangeRequest request) {
        return ResponseEntity.ok(userService.changeMyPassword(request));
    }
}