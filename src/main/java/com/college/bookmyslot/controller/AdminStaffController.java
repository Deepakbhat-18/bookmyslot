package com.college.bookmyslot.controller;

import com.college.bookmyslot.dto.ApiResponse;
import com.college.bookmyslot.model.User;
import com.college.bookmyslot.repository.UserRepository;
import com.college.bookmyslot.service.EmailService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public AdminStaffController(
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    @GetMapping
    public List<User> getAllClubStaff() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == User.Role.CLUB)
                .toList();
    }


    @GetMapping("/{staffId}")
    public User getStaffById(@PathVariable Long staffId) {
        return userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
    }


    @PutMapping("/{staffId}/deactivate")
    public ApiResponse<String> deactivateStaff(@PathVariable Long staffId) {

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        staff.setActive(false);
        userRepository.save(staff);

        return new ApiResponse<>(
                true,
                "Staff deactivated successfully",
                null
        );
    }


    @PostMapping("/{staffId}/reset-password")
    public ApiResponse<String> resetStaffPassword(@PathVariable Long staffId) {

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        staff.setResetOtp(otp);
        staff.setResetOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(staff);

        emailService.sendOtpEmail(
                staff.getEmail(),
                staff.getName(),
                otp,
                "Staff Password Reset"
        );

        return new ApiResponse<>(
                true,
                "Password reset OTP sent to staff email",
                null
        );
    }
}
