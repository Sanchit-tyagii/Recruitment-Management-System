package com.recruitment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "jobs")
public class Job {
    @Id
    private String id;
    private String title;
    private String description;
    private String companyName;
    private LocalDateTime postedOn = LocalDateTime.now();
    private int totalApplications = 0;

    @DBRef
    private User postedBy;
}
