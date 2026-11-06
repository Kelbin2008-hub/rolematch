package com.rolematch.service;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Skill;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.repository.JobRepository;
import com.rolematch.repository.SkillRepository;
import com.rolematch.repository.UserRepository;
import com.rolematch.web.dto.JobCreateRequest;
import com.rolematch.web.dto.JobViewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public List<JobViewDto> getActiveJobs() {
        return jobRepository.findActiveJobsWithSkills(JobStatus.OPEN).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Returns jobs owned by the given recruiter — used in recruiter portal to enforce ownership scope.
     */
    public List<JobViewDto> getJobsForRecruiter(Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new NoSuchElementException("Recruiter not found with ID: " + recruiterId));
        return jobRepository.findByRecruiter(recruiter).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public JobViewDto getJobById(Long id) {
        return jobRepository.findByIdWithSkills(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Job posting not found for ID: " + id));
    }

    public Job getJobEntity(Long id) {
        return jobRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new NoSuchElementException("Job posting not found for ID: " + id));
    }

    @Transactional
    public JobViewDto createJob(JobCreateRequest request, Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new NoSuchElementException("Recruiter not found with ID: " + recruiterId));

        if (!UserRole.RECRUITER.equals(recruiter.getRole())) {
            throw new IllegalArgumentException("User ID " + recruiterId + " is not authorized as a RECRUITER.");
        }

        Job job = new Job(
                recruiter,
                request.title(),
                request.company(),
                request.minSalary(),
                request.maxSalary(),
                request.description(),
                JobStatus.OPEN,
                request.department() != null && !request.department().isBlank() ? request.department() : "Engineering & Infrastructure",
                request.location() != null && !request.location().isBlank() ? request.location() : "Remote Node (Global Epoch)",
                request.atsThreshold() != null ? request.atsThreshold() : 75
        );

        if (request.skills() != null && !request.skills().isEmpty()) {
            for (String skillName : request.skills()) {
                if (skillName != null && !skillName.isBlank()) {
                    Skill skill = skillRepository.findByNameIgnoreCase(skillName.trim())
                            .orElseGet(() -> skillRepository.save(new Skill(skillName.trim())));
                    job.addSkill(skill);
                }
            }
        }

        Job saved = jobRepository.save(job);
        return toDto(saved);
    }

    public JobViewDto toDto(Job job) {
        List<String> skills = job.getSkills().stream()
                .map(Skill::getName)
                .sorted()
                .toList();

        return new JobViewDto(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getMinSalary(),
                job.getMaxSalary(),
                job.getDescription(),
                job.getStatus(),
                skills,
                job.getRecruiter().getId(),
                job.getRecruiter().getName(),
                job.getCreatedAt(),
                job.getDepartment(),
                job.getLocation(),
                job.getAtsThreshold()
        );
    }
}
