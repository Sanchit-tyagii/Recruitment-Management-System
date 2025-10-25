package com.recruitment.controller;

import com.recruitment.model.Job;
import com.recruitment.model.Profile;
import com.recruitment.model.UserType;
import com.recruitment.repository.JobRepository;
import com.recruitment.repository.UserRepository;
import com.recruitment.service.JobService;
import com.recruitment.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ApplicantController {

    private final ResumeService resumeService;
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicantController(ResumeService resumeService, JobService jobService,
                               JobRepository jobRepository, UserRepository userRepository) {
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/uploadResume")
    public ResponseEntity<?> uploadResume(HttpServletRequest req,
                                          @RequestParam("file") MultipartFile file) throws IOException {
        var userTypeAttr = req.getAttribute("currentUserType");
        if (userTypeAttr == null || !userTypeAttr.equals(UserType.APPLICANT.name())) {
            return ResponseEntity.status(403).body("Only applicants can upload resume");
        }

        String userId = String.valueOf(req.getAttribute("currentUserId"));
        Profile profile = resumeService.uploadAndParseResume(userId, file);

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> listJobs() {
        return ResponseEntity.ok(jobService.listJobs());
    }

    @GetMapping("/jobs/apply")
    public ResponseEntity<?> applyToJob(HttpServletRequest req, @RequestParam("job_id") String
            jobId) {
        var userTypeAttr = req.getAttribute("currentUserType");
        if (userTypeAttr == null || !userTypeAttr.equals(UserType.APPLICANT.name())) {
            return ResponseEntity.status(403).body("Only applicants can apply");
        }
        Long userId = (Long) req.getAttribute("currentUserId");
        Job job = jobService.getJob(jobId);
        // For assignment: increment count and return success. A full solution would persist an application record.
        jobService.incrementApplications(job.getId());
        return ResponseEntity.ok("Applied successfully by userId: " + userId);
    }
}
