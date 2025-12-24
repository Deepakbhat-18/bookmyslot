package com.college.bookmyslot.dto;

import lombok.Data;

@Data
public class ClubCreateRequest {
    private String name;
    private String email;
    private String description;
}
