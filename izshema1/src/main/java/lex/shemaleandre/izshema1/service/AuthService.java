package lex.shemaleandre.izshema1.service;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.dto.LoginRequest;
import lex.shemaleandre.izshema1.dto.PasswordResetRequest;
import lex.shemaleandre.izshema1.dto.RegisterRequest;
import lex.shemaleandre.izshema1.entity.PasswordResetToken;
import lex.shemaleandre.izshema1.entity.User;
import lex.shemaleandre.izshema1.repository.PasswordResetTokenRepository;
import lex.shemaleandre.izshema1.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationProvider authenticationProvider;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationProvider authenticationProvider,
                       EmailService emailService,
                       PasswordResetTokenRepository tokenRepository,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationProvider = authenticationProvider;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
    }

    public ApiResponse registerStudent(RegisterRequest request) throws MessagingException {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.STUDENT);
        user.setBio(request.getBio());
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Welcome to Online Learning Platform",
                "Your account has been created successfully!");

        return new ApiResponse("Student registered successfully", user.getId());
    }

    public ApiResponse registerInstructor(RegisterRequest request) throws MessagingException {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.INSTRUCTOR);
        user.setBio(request.getBio());
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Welcome to Online Learning Platform",
                "Your instructor account has been created successfully!");

        return new ApiResponse("Instructor registered successfully", user.getId());
    }

    public ApiResponse login(LoginRequest request) {
        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getEmail()));
        return new ApiResponse("Login successful", Map.of(
                "jwt", jwt,
                "userId", user.getId(),
                "role", user.getRole().name()
        ));
    }

    public ApiResponse requestPasswordReset(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request",
                "Click here to reset your password: <a href=\"" + resetUrl + "\">Reset Password</a>");

        return new ApiResponse("Password reset link sent to your email");
    }

    public ApiResponse resetPassword(PasswordResetRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken());
        if (token == null || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(token);
        return new ApiResponse("Password reset successfully");
    }
}