package com.example.backend.repositories.student;

import com.example.backend.entities.student.StudentCoursePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCoursePurchaseRepository
        extends JpaRepository<StudentCoursePurchase, Long> {

    /**
     * ดึงรายการซื้อคอร์สของนักเรียนที่ยัง active
     */
    List<StudentCoursePurchase> findByStudentIdAndIsActiveTrue(Long studentId);

    /**
     * ดึงรายการซื้อคอร์สทั้งหมดของนักเรียน (รวม inactive)
     * ใช้กรณี report / audit
     */
    List<StudentCoursePurchase> findByStudentId(Long studentId);

    /**
     * เช็คว่านักเรียนเคยซื้อวิชานี้แล้วหรือไม่ (เฉพาะ active)
     * ใช้ป้องกันการซื้อซ้ำ (ถ้าต้องการ)
     */
    boolean existsByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(
            Long studentId,
            Long subjectId,
            String classType
    );

    List<StudentCoursePurchase> findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(Long studentId, Long subjectId, String classType);

    List<StudentCoursePurchase> findByIsActiveTrue();
}
