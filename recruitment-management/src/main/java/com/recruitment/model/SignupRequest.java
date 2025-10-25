package com.recruitment.model;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String userType; // ADMIN or APPLICANT
    private String profileHeadline;
    private String address;
    private String skills;
    private String education;
    private String experience;
    private String phone;
    private String resumeFilePath;
}
