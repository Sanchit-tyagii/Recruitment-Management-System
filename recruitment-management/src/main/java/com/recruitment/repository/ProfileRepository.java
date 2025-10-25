package com.recruitment.repository;

import com.recruitment.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProfileRepository extends MongoRepository<Profile, String> {
}

