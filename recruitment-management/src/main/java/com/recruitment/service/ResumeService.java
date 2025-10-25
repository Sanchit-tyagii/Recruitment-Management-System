package com.recruitment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.model.Profile;
import com.recruitment.model.User;
import com.recruitment.repository.ProfileRepository;
import com.recruitment.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class ResumeService {

    private final WebClient webClient;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final Path uploadDir;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResumeService(@Value("${resume-parser.url}") String apiUrl,
                         @Value("${resume-parser.apikey}") String apiKey,
                         @Value("${file.upload-dir}") String uploadDir,
                         ProfileRepository profileRepository,
                         UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.uploadDir = Paths.get(uploadDir);
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    public Profile uploadAndParseResume(String applicantId, MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName == null ||
                    !(fileName.endsWith(".pdf") || fileName.endsWith(".docx"))) {
                throw new RuntimeException("Only PDF or DOCX files are allowed");
            }

            User user = userRepository.findById(applicantId)
                    .orElseThrow(() -> new RuntimeException("Applicant not found"));

            String storedName = System.currentTimeMillis() + "_" + fileName;
            Path target = uploadDir.resolve(storedName);
            Files.write(target, file.getBytes(), StandardOpenOption.CREATE);

            String response = webClient.post()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = mapper.readTree(response);

            Profile profile = profileRepository.findById(user.getId())
                    .orElse(Profile.builder().userId(user.getId()).build());
               Profile.builder()
                    .userId(user.getId())
                    .resumeFilePath(target.toString())
                    .skills(root.has("skills") ? root.get("skills").toString() : null)
                    .education(root.has("education") ? root.get("education").toString() : null)
                    .experience(root.has("experience") ? root.get("experience").toString() : null)
                    .email(root.has("email") ? root.get("email").asText() : user.getEmail())
                    .name(root.has("name") ? root.get("name").asText() : user.getName())
                    .phone(root.has("phone") ? root.get("phone").asText() : null)
                    .build();

            return profileRepository.save(profile);
        } catch (IOException e) {
            throw new RuntimeException("Resume upload failed: " + e.getMessage(), e);
        }
    }
}


