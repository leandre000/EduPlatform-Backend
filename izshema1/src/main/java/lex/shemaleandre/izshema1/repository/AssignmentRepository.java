package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for Assignment entity
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
}