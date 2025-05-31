package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

// Repository for Complaint entity
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    List<Complaint> findByStudentId(UUID studentId);
    List<Complaint> findByCourseInstructorId(UUID instructorId);
}