package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for PasswordResetToken entity
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);
}