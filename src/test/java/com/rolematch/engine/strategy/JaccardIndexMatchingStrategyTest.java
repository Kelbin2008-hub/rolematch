package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.Skill;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.engine.model.MatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Jaccard Index Matching Strategy (Set Theory)")
class JaccardIndexMatchingStrategyTest {

    private JaccardIndexMatchingStrategy strategy;
    private User recruiter;
    private User seeker;

    @BeforeEach
    void setUp() {
        strategy = new JaccardIndexMatchingStrategy();
        recruiter = new User("Alice Recruiter", "alice@enterprise.com", "hash", UserRole.RECRUITER);
        seeker = new User("Bob Seeker", "bob@candidate.com", "hash", UserRole.SEEKER);
    }

    @Test
    @DisplayName("Should return 100% when resume skills perfectly match job skills")
    void shouldReturn100PercentOnExactMatch() {
        Job job = new Job(recruiter, "Senior Java Architect", "TechCorp",
                new BigDecimal("120000"), new BigDecimal("160000"),
                "Enterprise microservices role requiring Java, Spring Boot, and PostgreSQL", JobStatus.OPEN);
        job.setSkills(Set.of(new Skill("Java"), new Skill("Spring Boot"), new Skill("PostgreSQL")));

        Resume resume = new Resume(seeker, "bob_resume.pdf", 1024L,
                "Experienced backend architect proficient in Java, Spring Boot, and PostgreSQL.");
        resume.setSkillsSummary(List.of("Java", "Spring Boot", "PostgreSQL"));

        MatchResult result = strategy.evaluate(resume, job);

        assertThat(result.getOverallScore()).isEqualTo(100.0);
        assertThat(result.getRoundedScore()).isEqualTo((short) 100);
        assertThat(result.getMatchedSkills()).containsExactlyInAnyOrder("Java", "Spring Boot", "PostgreSQL");
        assertThat(result.getMissingSkills()).isEmpty();
    }

    @Test
    @DisplayName("Should calculate exact mathematical intersection over union for partial overlap")
    void shouldCalculateCorrectPartialOverlapRatio() {
        // Job requires: Java, Spring Boot, Docker, Kubernetes (4 skills)
        Job job = new Job(recruiter, "DevOps Engineer", "CloudInc",
                new BigDecimal("100000"), new BigDecimal("140000"),
                "Cloud infrastructure role", JobStatus.OPEN);
        job.setSkills(Set.of(new Skill("Java"), new Skill("Spring Boot"), new Skill("Docker"), new Skill("Kubernetes")));

        // Candidate has: Java, Spring Boot, Python, AWS (4 skills)
        // Intersection = {Java, Spring Boot} (size 2)
        // Union = {Java, Spring Boot, Docker, Kubernetes, Python, AWS} (size 6)
        // J(A, B) = 2 / 6 = 1/3 ~ 33.333%
        Resume resume = new Resume(seeker, "resume.pdf", 2048L, "Resume content");
        resume.setSkillsSummary(List.of("Java", "Spring Boot", "Python", "AWS"));

        MatchResult result = strategy.evaluate(resume, job);

        assertThat(result.getOverallScore()).isCloseTo(33.333, within(0.01));
        assertThat(result.getMatchedSkills()).containsExactlyInAnyOrder("Java", "Spring Boot");
        assertThat(result.getMissingSkills()).containsExactlyInAnyOrder("Docker", "Kubernetes");
        assertThat(result.getTelemetry().jaccardIntersectionSize()).isEqualTo(2);
        assertThat(result.getTelemetry().jaccardUnionSize()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should return 0% when candidate skills and job requirements are completely disjoint")
    void shouldReturnZeroOnDisjointSets() {
        Job job = new Job(recruiter, "Ruby Dev", "RubyCo",
                new BigDecimal("90000"), new BigDecimal("110000"), "Ruby on Rails", JobStatus.OPEN);
        job.setSkills(Set.of(new Skill("Ruby"), new Skill("Rails")));

        Resume resume = new Resume(seeker, "resume.pdf", 1000L, "Go developer");
        resume.setSkillsSummary(List.of("Go", "Kubernetes"));

        MatchResult result = strategy.evaluate(resume, job);

        assertThat(result.getOverallScore()).isEqualTo(0.0);
        assertThat(result.getMatchedSkills()).isEmpty();
        assertThat(result.getMissingSkills()).containsExactlyInAnyOrder("Ruby", "Rails");
    }
}
