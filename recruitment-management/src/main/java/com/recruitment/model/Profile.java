package com.recruitment.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "profiles")
@Builder
public class Profile {
    @Id
    private String id;
    private String userId;
    private String resumeFilePath;
    private String skills;
    private String education;
    private String experience;
    private String name;
    private String email;
    private String phone;
}
