package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.PasswordChangeRequest;
import lex.shemaleandre.izshema1.dto.UserDTO;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.exception.ResourceNotFoundException;
import lex.shemaleandre.izshema1.exception.UnauthorizedException;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for managing user-related operations
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get current user's profile
    public ApiResponse getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return new ApiResponse("Profile retrieved", mapToUserDTO(user));
    }

    // Update current user's profile
    public ApiResponse updateMyProfile(UserDTO userDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBio(userDTO.getBio());
        userRepository.save(user);
        return new ApiResponse("Profile updated successfully", mapToUserDTO(user));
    }

    // Change current user's password
    public ApiResponse changeMyPassword(PasswordChangeRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new ApiResponse("Password changed successfully");
    }

    // Get all users (Admin only)
    public ApiResponse getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());
        return new ApiResponse("Users retrieved", users);
    }

    // Update user role (Admin only)
    public ApiResponse updateUserRole(UUID userId, String role) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        try {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        userRepository.save(user);
        return new ApiResponse("User role updated successfully", mapToUserDTO(user));
    }

    // Delete a user (Admin only)
    public ApiResponse deleteUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
        return new ApiResponse("User deleted successfully");
    }

    // Map User entity to UserDTO
    private UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setBio(user.getBio());
        return dto;
    }
}