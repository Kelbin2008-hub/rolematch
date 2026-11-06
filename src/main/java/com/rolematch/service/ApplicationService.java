package com.rolematch.service;

import com.rolematch.domain.entity.Application;
import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.AppStatus;
import com.rolematch.event.CandidateShortlistedEvent;
import com.rolematch.repository.ApplicationRepository;
import com.rolematch.repository.JobRepository;
import com.rolematch.repository.ResumeRepository;
import com.rolematch.repository.UserRepository;
import com.rolematch.service.model.GeminiAnalysisResult;
import com.rolematch.web.dto.ApplicationViewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final GeminiAIService geminiAIService;
    private final ApplicationEventPublisher eventPublisher;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            ResumeRepository resumeRepository,
            GeminiAIService geminiAIService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.geminiAIService = geminiAIService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Pipeline Management Query: Fetches applications for a specific job, ownership-scoped to the recruiter.
     * Prevents IDOR: a recruiter who does not own the job receives an empty list.
     */
    public List<ApplicationViewDto> getRankedApplicationsForJob(Long jobId, Long recruiterId) {
        return applicationRepository.findApplicationsByJobAndRecruiterRanked(jobId, recruiterId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retained for test/internal use where job ownership is already established.
     */
    public List<ApplicationViewDto> getRankedApplicationsForJob(Long jobId) {
        return applicationRepository.findApplicationsByJobRankedByMatchScore(jobId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Pipeline Management Query: Fetches recruiter's candidate applications ranked by matchScore DESC.
     */
    public List<ApplicationViewDto> getRankedApplicationsForRecruiter(Long recruiterId) {
        return applicationRepository.findApplicationsByRecruiterRanked(recruiterId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationViewDto> getApplicationsForSeeker(Long seekerId) {
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new NoSuchElementException("Seeker not found with ID: " + seekerId));
        return applicationRepository.findBySeekerOrderByCreatedAtDesc(seeker).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationViewDto applyToJob(Long seekerId, Long jobId, Long resumeId) {
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new NoSuchElementException("Seeker not found with ID: " + seekerId));
        Job job = jobRepository.findByIdWithSkills(jobId)
                .orElseThrow(() -> new NoSuchElementException("Job not found with ID: " + jobId));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new NoSuchElementException("Resume not found with ID: " + resumeId));

        // Ownership check: prevent a seeker from applying with another seeker's resume (IDOR)
        if (!resume.getSeeker().getId().equals(seekerId)) {
            throw new SecurityException("Access Denied: This resume does not belong to the current user.");
        }

        Application application = applicationRepository.findByJobAndSeeker(job, seeker)
                .orElseGet(() -> new Application(job, seeker, resume));

        application.setResume(resume);

        // Perform AI analysis (falls back to deterministic engine if Gemini is unconfigured)
        GeminiAnalysisResult aiAnalysis = geminiAIService.analyze(resume, job);
        geminiAIService.applyAnalysisToEntities(resume, application, aiAnalysis);

        Application saved = applicationRepository.save(application);
        return toDto(saved);
    }

    /**
     * Updates application state and triggers Observer Pattern CandidateShortlistedEvent if SHORTLISTED.
     */
    @Transactional
    public ApplicationViewDto updateApplicationStatus(Long applicationId, AppStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("Application not found with ID: " + applicationId));

        AppStatus previousStatus = application.getStatus();
        application.setStatus(newStatus);
        Application saved = applicationRepository.save(application);

        log.info("Application ID {} status updated from {} to {}", applicationId, previousStatus, newStatus);

        // Observer Pattern: publish event when candidate is shortlisted
        if (AppStatus.SHORTLISTED.equals(newStatus) && !AppStatus.SHORTLISTED.equals(previousStatus)) {
            CandidateShortlistedEvent event = new CandidateShortlistedEvent(
                    application.getSeeker().getEmail(),
                    application.getSeeker().getName(),
                    application.getJob().getTitle(),
                    application.getJob().getCompany(),
                    application.getId(),
                    application.getMatchScore()
            );
            eventPublisher.publishEvent(event);
            log.info("Published CandidateShortlistedEvent for Seeker: {}", application.getSeeker().getEmail());
        }

        return toDto(saved);
    }

    /**
     * Ownership-enforcing variant: verifies that the application's job belongs to the given recruiter
     * before allowing a status transition. Throws SecurityException on IDOR attempt.
     */
    @Transactional
    public ApplicationViewDto updateApplicationStatusForRecruiter(Long applicationId, AppStatus newStatus, Long recruiterId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("Application not found with ID: " + applicationId));

        Long jobRecruiterId = application.getJob().getRecruiter().getId();
        if (!jobRecruiterId.equals(recruiterId)) {
            throw new SecurityException("Access Denied: You are not authorized to manage this application.");
        }

        return updateApplicationStatus(applicationId, newStatus);
    }

    public ApplicationViewDto toDto(Application app) {
        return new ApplicationViewDto(
                app.getId(),
                app.getJob().getId(),
                app.getJob().getTitle(),
                app.getJob().getCompany(),
                app.getSeeker().getId(),
                app.getSeeker().getName(),
                app.getSeeker().getEmail(),
                app.getResume().getId(),
                app.getResume().getFileName(),
                app.getMatchScore(),
                app.getStatus(),
                app.getMatchedSkills(),
                app.getMissingSkills(),
                app.getCreatedAt()
        );
    }
}
