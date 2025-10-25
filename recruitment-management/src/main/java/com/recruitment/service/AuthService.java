package com.recruitment.service;

import com.recruitment.config.JwtUtil;
import com.recruitment.model.LoginRequest;
import com.recruitment.model.Profile;
import com.recruitment.model.SignupRequest;
import com.recruitment.model.User;
import com.recruitment.model.UserType;
import com.recruitment.repository.ProfileRepository;
import com.recruitment.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.profileRepository = profileRepository;
    }

    public String signup(SignupRequest req) {
        if (req.getUserType() == null || (!req.getUserType().equalsIgnoreCase("ADMIN")
                && !req.getUserType().equalsIgnoreCase("APPLICANT"))) {
            throw new RuntimeException("User type must be ADMIN or APPLICANT");
        }

        Optional<User> existing = userRepository.findByEmail(req.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setAddress(req.getAddress());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setProfileHeadline(req.getProfileHeadline());
        user.setUserType(UserType.valueOf(req.getUserType().toUpperCase()));

        userRepository.save(user);

        if (user.getUserType() == UserType.APPLICANT) {
            Profile profile = Profile.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .skills(req.getSkills())
                    .education(req.getEducation())
                    .experience(req.getExperience())
                    .resumeFilePath(req.getResumeFilePath())
                    .build();
            profileRepository.save(profile);
        }

        return "User created successfully";
    }

    public String login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
