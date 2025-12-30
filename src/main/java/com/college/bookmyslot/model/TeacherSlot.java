package com.college.bookmyslot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "teacher_slots")
public class TeacherSlot {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Setter
    @Getter
    private LocalDate date;

    @Setter
    @Getter
    private LocalTime startTime;
    private LocalDate slotDate;
    @Setter
    @Getter
    private LocalTime endTime;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        AVAILABLE,
        BOOKED,
        BLOCKED
    }


}
