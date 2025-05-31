package lex.shemaleandre.izshema1.repository;

import lex.shemaleandre.izshema1.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByCourseId(UUID courseId);

    @Query("SELECT r FROM Review r WHERE r.student.id = :studentId AND r.course.id = :courseId")
    Optional<Review> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.student.id = :studentId AND r.course.id = :courseId")
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
}