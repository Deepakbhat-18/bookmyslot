package com.college.bookmyslot.repository;

import com.college.bookmyslot.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByEmail(String email);

    Optional<Club> findByName(String name);
}
