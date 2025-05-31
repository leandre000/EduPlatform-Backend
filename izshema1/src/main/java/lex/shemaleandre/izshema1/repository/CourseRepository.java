package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for Course entity
public interface CourseRepository extends JpaRepository<Course, UUID> {
}