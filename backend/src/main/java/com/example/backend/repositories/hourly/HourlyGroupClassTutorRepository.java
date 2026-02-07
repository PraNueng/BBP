package com.example.backend.repositories.hourly;

import com.example.backend.entities.User;
import com.example.backend.entities.hourly.HourlyGroupClass;
import com.example.backend.entities.hourly.HourlyGroupClassTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyGroupClassTutorRepository extends JpaRepository<HourlyGroupClassTutor, Long> {

    Optional<HourlyGroupClassTutor> findByHourlyGroupClassAndTutor(HourlyGroupClass hourlyGroupClass, User tutor);

    List<HourlyGroupClassTutor> findByHourlyGroupClass(HourlyGroupClass hourlyGroupClass);

    @Query("SELECT ct FROM HourlyGroupClassTutor ct WHERE ct.hourlyGroupClass.id = :classId")
    List<HourlyGroupClassTutor> findByClassId(@Param("classId") Long classId);

    List<HourlyGroupClassTutor> findByTutor(User tutor);

    @Query("SELECT ct FROM HourlyGroupClassTutor ct WHERE ct.tutor.id = :tutorId")
    List<HourlyGroupClassTutor> findByTutorId(@Param("tutorId") Long tutorId);

    boolean existsByHourlyGroupClassAndTutor(HourlyGroupClass hourlyGroupClass, User tutor);

    @Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END FROM HourlyGroupClassTutor ct " +
            "WHERE ct.hourlyGroupClass.id = :classId AND ct.tutor.id = :tutorId")
    boolean existsByClassIdAndTutorId(@Param("classId") Long classId, @Param("tutorId") Long tutorId);

    @Query("SELECT COUNT(ct) FROM HourlyGroupClassTutor ct WHERE ct.hourlyGroupClass.id = :classId")
    long countByClassId(@Param("classId") Long classId);

    @Query("SELECT COUNT(ct) FROM HourlyGroupClassTutor ct WHERE ct.tutor.id = :tutorId")
    long countByTutorId(@Param("tutorId") Long tutorId);

    void deleteByHourlyGroupClassAndTutor(HourlyGroupClass hourlyGroupClass, User tutor);

    void deleteByHourlyGroupClass(HourlyGroupClass hourlyGroupClass);

    @Query("SELECT ct FROM HourlyGroupClassTutor ct " +
            "LEFT JOIN FETCH ct.hourlyGroupClass " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE ct.hourlyGroupClass.id = :classId")
    List<HourlyGroupClassTutor> findByClassIdWithDetails(@Param("classId") Long classId);
}