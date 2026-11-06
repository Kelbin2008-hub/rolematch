package com.rolematch.domain.entity;

import com.rolematch.domain.enums.AppStatus;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JPA Domain Model Encapsulation & Validation Tests")
class EntityMappingTest {

    @Test
    @DisplayName("User entity should auto-generate UUID and enforce encapsulation")
    void testUserEncapsulation() {
        User user = new User("Jane Doe", "jane@company.com", "encoded-secret", UserRole.SEEKER);
        assertThat(user.getUuid()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("jane@company.com");
        assertThat(user.getActive()).isTrue();
        assertThat(user.isSeeker()).isTrue();

        // Immutability test on UUID
        assertThatThrownBy(() -> user.setUuid(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("immutable");

        // Validation on invalid email
        assertThatThrownBy(() -> user.setEmail("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Job entity should enforce salary range invariants")
    void testJobSalaryInvariants() {
        User recruiter = new User("Recruiter Mike", "mike@hiring.com", "hash", UserRole.RECRUITER);

        assertThatThrownBy(() -> new Job(recruiter, "Software Engineer", "Corp",
                new BigDecimal("150000"), new BigDecimal("100000"), "Description", JobStatus.OPEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed maximum salary");
    }

    @Test
    @DisplayName("Resume entity should encapsulate JSONB fields with defensive copying")
    void testResumeJsonbEncapsulation() {
        User seeker = new User("Alex", "alex@dev.io", "pass", UserRole.SEEKER);
        Resume resume = new Resume(seeker, "resume.pdf", 1024L, "Full stack developer");

        resume.setAtsBaseScore(88);
        resume.setSkillsSummary(List.of("Java", "PostgreSQL", "Docker"));
        resume.setImprovementSuggestions(List.of("Add metrics to work experience"));
        resume.setInterviewQuestions(List.of("Explain ACID in database transactions"));

        assertThat(resume.getAtsBaseScore()).isEqualTo(88);
        assertThat(resume.getSkillsSummary()).containsExactly("Java", "PostgreSQL", "Docker");
        assertThat(resume.getImprovementSuggestions()).hasSize(1);
        assertThat(resume.getInterviewQuestions()).hasSize(1);

        // Score validation
        assertThatThrownBy(() -> resume.setAtsBaseScore(120))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Application entity should encapsulate match scores and JSONB skill deltas")
    void testApplicationEncapsulation() {
        User recruiter = new User("Recruiter", "rec@company.com", "h", UserRole.RECRUITER);
        User seeker = new User("Seeker", "seek@candidate.com", "h", UserRole.SEEKER);
        Job job = new Job(recruiter, "Backend Lead", "FinTech",
                new BigDecimal("140000"), new BigDecimal("190000"), "Lead role", JobStatus.OPEN);
        Resume resume = new Resume(seeker, "res.pdf", 2048L, "Lead engineer");

        Application application = new Application(job, seeker, resume);
        application.setMatchScore((short) 92);
        application.setStatus(AppStatus.SHORTLISTED);
        application.setMatchedSkills(List.of("Java", "Spring Boot", "PostgreSQL"));
        application.setMissingSkills(List.of("Kubernetes"));

        assertThat(application.getMatchScore()).isEqualTo((short) 92);
        assertThat(application.getStatus()).isEqualTo(AppStatus.SHORTLISTED);
        assertThat(application.getMatchedSkills()).containsExactly("Java", "Spring Boot", "PostgreSQL");
        assertThat(application.getMissingSkills()).containsExactly("Kubernetes");

        assertThatThrownBy(() -> application.setMatchScore((short) 105))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Job entity should support department, location, and ATS threshold attributes")
    void testJobAttributesAndDefaults() {
        User recruiter = new User("Recruiter", "rec@company.com", "h", UserRole.RECRUITER);
        Job job = new Job(
                recruiter,
                "Cloud Systems Architect",
                "Stripe Platform",
                new BigDecimal("185000"),
                new BigDecimal("230000"),
                "Event-driven cloud infrastructure",
                JobStatus.OPEN,
                "Infrastructure & Scalability",
                "San Francisco Hub / Distributed",
                85
        );

        assertThat(job.getDepartment()).isEqualTo("Infrastructure & Scalability");
        assertThat(job.getLocation()).isEqualTo("San Francisco Hub / Distributed");
        assertThat(job.getAtsThreshold()).isEqualTo(85);

        job.setAtsThreshold(95);
        assertThat(job.getAtsThreshold()).isEqualTo(95);

        // Clamp behavior
        job.setAtsThreshold(150);
        assertThat(job.getAtsThreshold()).isEqualTo(100);
    }
}
