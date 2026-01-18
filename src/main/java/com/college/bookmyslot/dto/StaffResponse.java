package com.college.bookmyslot.dto;
import lombok.Data;
@Data
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String clubName;
    private boolean active;
}
