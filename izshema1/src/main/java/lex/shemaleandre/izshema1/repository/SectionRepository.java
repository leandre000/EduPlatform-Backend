package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// Repository for Section entity
public interface SectionRepository extends JpaRepository<Section, UUID> {
}