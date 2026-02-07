package com.example.backend.repositories.hourly;

import com.example.backend.entities.User;
import com.example.backend.entities.hourly.HourlyIndividualClass;
import com.example.backend.entities.hourly.HourlyIndividualClassTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyIndividualClassTutorRepository extends JpaRepository<HourlyIndividualClassTutor, Long> {

    Optional<HourlyIndividualClassTutor> findByHourlyIndividualClassAndTutor(
            HourlyIndividualClass hourlyIndividualClass, User tutor);

    List<HourlyIndividualClassTutor> findByHourlyIndividualClass(HourlyIndividualClass hourlyIndividualClass);

    @Query("SELECT ct FROM HourlyIndividualClassTutor ct WHERE ct.hourlyIndividualClass.id = :classId")
    List<HourlyIndividualClassTutor> findByClassId(@Param("classId") Long classId);

    List<HourlyIndividualClassTutor> findByTutor(User tutor);

    @Query("SELECT ct FROM HourlyIndividualClassTutor ct WHERE ct.tutor.id = :tutorId")
    List<HourlyIndividualClassTutor> findByTutorId(@Param("tutorId") Long tutorId);

    boolean existsByHourlyIndividualClassAndTutor(HourlyIndividualClass hourlyIndividualClass, User tutor);

    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END FROM HourlyIndividualClassTutor ct " +
            "WHERE ct.hourlyIndividualClass.id = :classId AND ct.tutor.id = :tutorId")
    boolean existsByClassIdAndTutorId(@Param("classId") Long classId, @Param("tutorId") Long tutorId);

    @Query("SELECT COUNT(ct) FROM HourlyIndividualClassTutor ct WHERE ct.hourlyIndividualClass.id = :classId")
    long countByClassId(@Param("classId") Long classId);

    @Query("SELECT COUNT(ct) FROM HourlyIndividualClassTutor ct WHERE ct.tutor.id = :tutorId")
    long countByTutorId(@Param("tutorId") Long tutorId);

    void deleteByHourlyIndividualClassAndTutor(HourlyIndividualClass hourlyIndividualClass, User tutor);

    void deleteByHourlyIndividualClass(HourlyIndividualClass hourlyIndividualClass);

    @Query("SELECT ct FROM HourlyIndividualClassTutor ct " +
            "LEFT JOIN FETCH ct.hourlyIndividualClass " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE ct.hourlyIndividualClass.id = :classId")
    List<HourlyIndividualClassTutor> findByClassIdWithDetails(@Param("classId") Long classId);

    /**
     * ลบครูออกจากคลาส
     */
    @Modifying
    @Query("DELETE FROM HourlyIndividualClassTutor ct " +
            "WHERE ct.hourlyIndividualClass.id = :classId " +
            "AND ct.tutor.id = :tutorId")
    void deleteByClassIdAndTutorId(@Param("classId") Long classId, @Param("tutorId") Long tutorId);
}