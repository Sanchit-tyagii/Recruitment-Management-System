package com.recruitment.service;

import com.recruitment.exception.ResourceNotFoundException;
import com.recruitment.model.Job;
import com.recruitment.model.User;
import com.recruitment.repository.JobRepository;
import com.recruitment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public Job createJob(Job job, String postedById) {
        User admin = userRepository.findById(postedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + postedById));
        job.setPostedBy(admin);
        job.setPostedOn(LocalDateTime.now());
        job.setTotalApplications(0);
        return jobRepository.save(job);
    }

    public Job getJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
    }

    public List<Job> listJobs() {
        return jobRepository.findAll();
    }

    public Job applyJob(String jobId, String applicantId) {
        Job job = getJob(jobId);
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found: " + applicantId));
        job.setTotalApplications(job.getTotalApplications() + 1);
        return jobRepository.save(job);
    }
    public void incrementApplications(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        job.setTotalApplications(job.getTotalApplications() + 1);
        jobRepository.save(job);
    }
}

