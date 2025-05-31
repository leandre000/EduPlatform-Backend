package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.MarkingGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for MarkingGuide entity
public interface MarkingGuideRepository extends JpaRepository<MarkingGuide, UUID> {
}