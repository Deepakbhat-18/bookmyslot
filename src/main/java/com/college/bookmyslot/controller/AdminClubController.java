package com.college.bookmyslot.controller;

import com.college.bookmyslot.dto.ApiResponse;
import com.college.bookmyslot.dto.ClubCreateRequest;
import com.college.bookmyslot.dto.ClubStaffCreateRequest;
import com.college.bookmyslot.model.Club;
import com.college.bookmyslot.model.User;
import com.college.bookmyslot.repository.ClubRepository;
import com.college.bookmyslot.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/clubs")
@CrossOrigin(origins = "*")
public class AdminClubController {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    public AdminClubController(ClubRepository clubRepository,UserRepository userRepository ) {
        this.clubRepository = clubRepository;
        this.userRepository=userRepository;
    }


    @PostMapping
    public ApiResponse<Club> createClub(
            @RequestBody ClubCreateRequest request
    ) {

        if (clubRepository.existsByName(request.getName())) {
            throw new RuntimeException("Club name already exists");
        }

        if (clubRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Club email already exists");
        }

        Club club = new Club();
        club.setName(request.getName());
        club.setEmail(request.getEmail());
        club.setDescription(request.getDescription());

        Club saved = clubRepository.save(club);

        return new ApiResponse<>(
                true,
                "Club created successfully",
                saved
        );
    }
    @PostMapping("/staff")
    public ApiResponse<String> createClubStaff(
            @RequestBody ClubStaffCreateRequest request
    ) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        if (userRepository.existsByClub(club)) {
            throw new RuntimeException("This club already has a staff");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User staff = new User();
        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPassword(request.getPassword()); // plain for now
        staff.setRole(User.Role.CLUB);
        staff.setClub(club);
        staff.setVerified(true); // admin-created → auto verified

        userRepository.save(staff);

        return new ApiResponse<>(
                true,
                "Club staff created successfully",
                null
        );
    }

}
