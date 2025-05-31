package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for Lecture entity
public interface LectureRepository extends JpaRepository<Lecture, UUID> {
}