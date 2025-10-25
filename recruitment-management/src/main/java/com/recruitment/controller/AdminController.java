package com.recruitment.controller;

import com.recruitment.model.Job;
import com.recruitment.model.Profile;
import com.recruitment.model.User;
import com.recruitment.repository.ProfileRepository;
import com.recruitment.repository.UserRepository;
import com.recruitment.service.JobService;
import com.recruitment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final JobService jobService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public AdminController(JobService jobService, UserService userService,
                           UserRepository userRepository, ProfileRepository profileRepository) {
        this.jobService = jobService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    private boolean isAdmin(HttpServletRequest req) {
        Object typeObj = req.getAttribute("currentUserType");
        System.out.println("CurrentUserType attribute: " + typeObj);
        if (typeObj == null) return false;
        String type = typeObj.toString();
        return "ADMIN".equalsIgnoreCase(type);
    }

    private Object getCurrentUserId(HttpServletRequest req) {
        String idObj = req.getAttribute("currentUserId").toString();
        if (idObj == null) return null;


        return idObj;
    }


    @PostMapping("/job")
    public ResponseEntity<?> createJob(HttpServletRequest req, @RequestBody com.recruitment.model.JobRequest jr) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Admin only");
        String adminId = (String) getCurrentUserId(req);
        if (adminId == null) return ResponseEntity.status(400).body("Invalid user id");

        Job job = new Job();
        job.setTitle(jr.getTitle());
        job.setDescription(jr.getDescription());
        job.setCompanyName(jr.getCompanyName());

        Job saved = jobService.createJob(job, adminId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/job/{job_id}")
    public ResponseEntity<?> getJob(HttpServletRequest req, @PathVariable("job_id") String jobId) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Admin only");

        Job job = jobService.getJob(jobId);
        List<Profile> profiles = profileRepository.findAll();
        return ResponseEntity.ok(new JobDetailResponse(job, profiles));
    }

    @GetMapping("/applicants")
    public ResponseEntity<?> getAllUsers(HttpServletRequest req) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Admin only");

        List<User> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/applicant/{applicant_id}")
    public ResponseEntity<?> getApplicant(HttpServletRequest req, @PathVariable("applicant_id") String applicantId) {
        if (!isAdmin(req)) return ResponseEntity.status(403).body("Admin only");

        Profile profile = userService.getApplicantProfile(applicantId);
        return ResponseEntity.ok(profile);
    }
    static record JobDetailResponse(Job job, List<Profile> applicants) {}
}
