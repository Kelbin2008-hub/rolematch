package com.rolematch.config;

import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.AppStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.repository.JobRepository;
import com.rolematch.repository.ResumeRepository;
import com.rolematch.service.ApplicationService;
import com.rolematch.service.JobService;
import com.rolematch.service.UserService;
import com.rolematch.web.dto.ApplicationViewDto;
import com.rolematch.web.dto.JobCreateRequest;
import com.rolematch.web.dto.JobViewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Automatically seeds initial demo users, calibrated job requisitions,
 * and verified candidate application telemetry if the database starts empty.
 */
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final JobService jobService;
    private final UserService userService;
    private final ApplicationService applicationService;

    public DataInitializer(
            JobRepository jobRepository,
            ResumeRepository resumeRepository,
            JobService jobService,
            UserService userService,
            ApplicationService applicationService
    ) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.jobService = jobService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @Override
    public void run(String... args) {
        if (jobRepository.count() == 0) {
            log.info("Database is empty. Seeding initial demo users and calibrated job requisitions...");

            User recruiter = userService.findOrCreateDemoUser(
                    "recruiter.demo@rolematch.ai",
                    "Elena Vance (Lead Recruiter)",
                    UserRole.RECRUITER
            );

            User candidate = userService.findOrCreateDemoUser(
                    "candidate.demo@rolematch.ai",
                    "Alex Rivera (Candidate)",
                    UserRole.SEEKER
            );

            // Seed Job 1: Distributed Systems
            JobViewDto job1 = jobService.createJob(new JobCreateRequest(
                    "Principal Cloud & Distributed Systems Engineer",
                    "Stripe Platform Infrastructure",
                    BigDecimal.valueOf(185000),
                    BigDecimal.valueOf(240000),
                    "Architect and scale multi-region event-driven microservices processing high transaction concurrency. Oversee Kafka stream topologies, PostgreSQL sharding, and resilience patterns.",
                    List.of("Java 21", "Spring Boot 3", "Apache Kafka", "PostgreSQL", "Docker", "Kubernetes"),
                    "Platform & Scalability",
                    "San Francisco Hub / Distributed UTC-8 to UTC-4",
                    85
            ), recruiter.getId());

            // Seed Job 2: Microservices Backend
            JobViewDto job2 = jobService.createJob(new JobCreateRequest(
                    "Senior Backend Microservices Architect",
                    "FinTech Global Payments",
                    BigDecimal.valueOf(160000),
                    BigDecimal.valueOf(210000),
                    "Lead development of low-latency transaction orchestration engines utilizing Spring Boot 3.3 and Resilience4j circuit breakers. Maintain high test coverage with JUnit 5 and Testcontainers.",
                    List.of("Spring Boot 3", "Microservices", "RESTful APIs", "AWS", "JUnit 5", "Distributed Tracing"),
                    "Payment Processing Pod",
                    "New York, NY / Hybrid",
                    80
            ), recruiter.getId());

            // Seed Job 3: AI & Data Pipeline
            jobService.createJob(new JobCreateRequest(
                    "Staff AI & Data Pipeline Engineer",
                    "Neural Match AI Labs",
                    BigDecimal.valueOf(175000),
                    BigDecimal.valueOf(230000),
                    "Integrate LLM semantic inference endpoints and vectorize candidate text pipelines. Build real-time vector indexing services with PostgreSQL and Spring Boot.",
                    List.of("Java 21", "Spring Boot 3", "PostgreSQL", "Kafka", "Python", "Docker"),
                    "Applied AI & Vector Core",
                    "Remote Node (Global Epoch)",
                    82
            ), recruiter.getId());

            // Seed Candidate Resume AST
            Resume resume = resumeRepository.save(new Resume(
                    candidate,
                    "Alex_Rivera_Production_Resume.pdf",
                    1842000L,
                    "Alex Rivera. Staff Backend Architect with 8+ years experience in Java 21, Spring Boot 3, Apache Kafka, PostgreSQL, Docker, Microservices, and Cloud Infrastructure."
            ));
            resume.setAtsBaseScore(88);
            resume.setSkillsSummary(List.of("Java 21", "Spring Boot 3", "PostgreSQL", "Apache Kafka", "Docker", "Microservices", "RESTful APIs", "JUnit 5"));
            resume.setImprovementSuggestions(List.of("Kubernetes Helm", "AWS DynamoDB", "Distributed Tracing"));
            resume.setInterviewQuestions(List.of(
                    "How would you migrate a synchronous REST pipeline to an event-driven Kafka architecture without data loss during peak load?",
                    "Describe your approach to optimizing slow PostgreSQL queries with composite indexes in high-throughput tables.",
                    "Explain your strategy for graceful degradation in Spring Boot microservices when dependent downstream endpoints time out."
            ));
            resumeRepository.save(resume);

            // Seed Initial Pipeline Submissions
            try {
                ApplicationViewDto app1 = applicationService.applyToJob(candidate.getId(), job1.id(), resume.getId());
                applicationService.updateApplicationStatus(app1.id(), AppStatus.SHORTLISTED);

                applicationService.applyToJob(candidate.getId(), job2.id(), resume.getId());
                log.info("Successfully seeded demo applications and pipeline records.");
            } catch (Exception e) {
                log.warn("Application seed notice: {}", e.getMessage());
            }

            log.info("Successfully seeded 3 verified engineering requisitions, candidate resume, and demo personas.");
        }
    }
}
