package com.college.bookmyslot.repository;

import com.college.bookmyslot.model.SlotBooking;
import com.college.bookmyslot.model.TeacherSlot;
import com.college.bookmyslot.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TeacherSlotRepository extends JpaRepository<TeacherSlot, Long> {

    List<TeacherSlot> findByTeacher(User teacher);

    List<TeacherSlot> findByTeacherAndDate(User teacher, LocalDate date);

    List<TeacherSlot> findByDateAndStatus(LocalDate date, TeacherSlot.Status status);

    long countByStatus(TeacherSlot.Status status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM TeacherSlot s WHERE s.id = :slotId")
    Optional<TeacherSlot> findByIdForUpdate(@Param("slotId") Long slotId);


}
