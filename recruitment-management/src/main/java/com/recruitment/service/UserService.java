package com.recruitment.service;

import com.recruitment.exception.ResourceNotFoundException;
import com.recruitment.model.Profile;
import com.recruitment.model.User;
import com.recruitment.repository.ProfileRepository;
import com.recruitment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public UserService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public Profile getApplicantProfile(String applicantId) {
        return profileRepository.findById(String.valueOf(applicantId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for applicant: " + applicantId));
    }
}
