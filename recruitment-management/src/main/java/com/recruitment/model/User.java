package com.recruitment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    private String address;
    private String passwordHash;
    private UserType userType;
    private String profileHeadline;

    // 🔥 Removed circular reference to Profile
}
