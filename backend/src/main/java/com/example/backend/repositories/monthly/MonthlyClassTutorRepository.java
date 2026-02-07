package com.example.backend.repositories.monthly;

import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.monthly.MonthlyClassTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyClassTutorRepository extends JpaRepository<MonthlyClassTutor, Long> {

    /**
     * ค้นหาความสัมพันธ์ระหว่างคลาสกับครู
     */
    Optional<MonthlyClassTutor> findByMonthlyClassAndTutor(MonthlyClass monthlyClass, User tutor);

    /**
     * ค้นหาครูทั้งหมดในคลาส
     */
    List<MonthlyClassTutor> findByMonthlyClass(MonthlyClass monthlyClass);

    /**
     * ค้นหาครูทั้งหมดในคลาส (ด้วย class ID)
     */
    @Query("SELECT ct FROM MonthlyClassTutor ct WHERE ct.monthlyClass.id = :classId")
    List<MonthlyClassTutor> findByClassId(@Param("classId") Long classId);

    /**
     * ค้นหาคลาสทั้งหมดของครู
     */
    List<MonthlyClassTutor> findByTutor(User tutor);

    /**
     * ค้นหาคลาสทั้งหมดของครู (ด้วย tutor ID)
     */
    @Query("SELECT ct FROM MonthlyClassTutor ct WHERE ct.tutor.id = :tutorId")
    List<MonthlyClassTutor> findByTutorId(@Param("tutorId") Long tutorId);

    /**
     * ตรวจสอบว่าครูคนนี้สอนคลาสนี้อยู่หรือไม่
     */
    boolean existsByMonthlyClassAndTutor(MonthlyClass monthlyClass, User tutor);

    /**
     * ตรวจสอบว่าครูคนนี้สอนคลาสนี้อยู่หรือไม่ (ด้วย ID)
     */
    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END FROM MonthlyClassTutor ct " +
            "WHERE ct.monthlyClass.id = :classId AND ct.tutor.id = :tutorId")
    boolean existsByClassIdAndTutorId(@Param("classId") Long classId, @Param("tutorId") Long tutorId);

    /**
     * นับจำนวนครูในคลาส
     */
    @Query("SELECT COUNT(ct) FROM MonthlyClassTutor ct WHERE ct.monthlyClass.id = :classId")
    long countByClassId(@Param("classId") Long classId);

    /**
     * นับจำนวนคลาสของครู
     */
    @Query("SELECT COUNT(ct) FROM MonthlyClassTutor ct WHERE ct.tutor.id = :tutorId")
    long countByTutorId(@Param("tutorId") Long tutorId);

    /**
     * ลบครูออกจากคลาส
     */
    void deleteByMonthlyClassAndTutor(MonthlyClass monthlyClass, User tutor);

    /**
     * ลบครูทั้งหมดออกจากคลาส
     */
    void deleteByMonthlyClass(MonthlyClass monthlyClass);

    /**
     * ค้นหาครูพร้อม fetch class และ tutor
     */
    @Query("SELECT ct FROM MonthlyClassTutor ct " +
            "LEFT JOIN FETCH ct.monthlyClass " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE ct.monthlyClass.id = :classId")
    List<MonthlyClassTutor> findByClassIdWithDetails(@Param("classId") Long classId);
}