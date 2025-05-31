package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for User entity
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
}