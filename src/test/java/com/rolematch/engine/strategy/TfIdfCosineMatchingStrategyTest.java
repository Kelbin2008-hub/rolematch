package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.engine.model.MatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("TF-IDF Cosine Similarity Matching Strategy (NLP & Linear Algebra)")
class TfIdfCosineMatchingStrategyTest {

    private TfIdfCosineMatchingStrategy strategy;
    private User recruiter;
    private User seeker;

    @BeforeEach
    void setUp() {
        strategy = new TfIdfCosineMatchingStrategy();
        recruiter = new User("Alice Recruiter", "alice@enterprise.com", "hash", UserRole.RECRUITER);
        seeker = new User("Bob Seeker", "bob@candidate.com", "hash", UserRole.SEEKER);
    }

    @Test
    @DisplayName("Should yield 100% cosine similarity when resume text is identical to job description")
    void shouldYield100PercentOnIdenticalTexts() {
        String sharedText = "Java enterprise backend development with Spring Boot, PostgreSQL, Docker, and REST APIs.";

        Job job = new Job(recruiter, "Java Engineer", "TechCorp",
                new BigDecimal("100000"), new BigDecimal("130000"), sharedText, JobStatus.OPEN);

        Resume resume = new Resume(seeker, "resume.pdf", 1024L, sharedText);

        MatchResult result = strategy.evaluate(resume, job);

        assertThat(result.getOverallScore()).isCloseTo(100.0, within(0.01));
    }

    @Test
    @DisplayName("Should yield high cosine similarity (>60%) when resume extracted text matches job description closely")
    void shouldYieldHighSimilarityOnAlignedTexts() {
        String jobText = """
                We are seeking a Senior Java Enterprise Architect. The candidate must have extensive
                experience designing distributed microservices with Java, Spring Boot, Spring Data JPA,
                PostgreSQL, Hibernate, Docker, Kubernetes, and REST APIs. Expertise in OOP principles,
                SOLID design patterns, and high-throughput scalable database architecture is required.
                """;

        String resumeText = """
                Accomplished Senior Java Enterprise Architect with 10 years of experience designing
                cloud-native microservices using Java 21, Spring Boot, Spring Data JPA, PostgreSQL,
                Hibernate, Docker, Kubernetes, and REST APIs. Deep mastery of OOP, SOLID principles,
                and scalable database architecture.
                """;

        Job job = new Job(recruiter, "Senior Java Architect", "FinTech Corp",
                new BigDecimal("140000"), new BigDecimal("180000"), jobText, JobStatus.OPEN);

        Resume resume = new Resume(seeker, "architect_resume.pdf", 4096L, resumeText);

        MatchResult result = strategy.evaluate(resume, job);

        assertThat(result.getOverallScore()).isGreaterThan(60.0);
        assertThat(result.getTelemetry().dotProduct()).isGreaterThan(0.0);
        assertThat(result.getTelemetry().vectorNormA()).isGreaterThan(0.0);
        assertThat(result.getTelemetry().vectorNormB()).isGreaterThan(0.0);
        assertThat(result.getMatchedSkills()).isNotEmpty();
    }

    @Test
    @DisplayName("Should yield near-zero cosine similarity for completely orthogonal vocabulary")
    void shouldYieldZeroForCompletelyDisjointText() {
        String jobText = "Java enterprise backend development with postgresql and spring framework";
        String resumeText = "Culinary chef specialized in French pastry baking sourdough bread and confectionery cakes";

        Job job = new Job(recruiter, "Java Dev", "TechCorp",
                new BigDecimal("90000"), new BigDecimal("120000"), jobText, JobStatus.OPEN);

        Resume resume = new Resume(seeker, "chef.pdf", 1024L, resumeText);

        MatchResult result = strategy.evaluate(resume, job);

        assertThat(result.getOverallScore()).isLessThan(5.0);
    }
}
