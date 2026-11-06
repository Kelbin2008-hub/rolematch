package com.rolematch.domain.entity;

import com.rolematch.domain.enums.AppStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Enterprise Application entity linking candidate, resume, and target role with algorithmic
 * match telemetry and workflow progression state.
 */
@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_application_job_match", columnList = "job_id, match_score"),
        @Index(name = "idx_application_seeker", columnList = "seeker_id"),
        @Index(name = "idx_application_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_job_seeker_application", columnNames = {"job_id", "seeker_id"})
})
public class Application extends AuditableEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seeker_id", nullable = false)
    private User seeker;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Min(0)
    @Max(100)
    @Column(name = "match_score")
    private Short matchScore;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AppStatus status = AppStatus.APPLIED;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "matched_skills", columnDefinition = "jsonb")
    private List<String> matchedSkills = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "missing_skills", columnDefinition = "jsonb")
    private List<String> missingSkills = new ArrayList<>();

    public Application() {
        this.status = AppStatus.APPLIED;
    }

    public Application(Job job, User seeker, Resume resume) {
        this();
        setJob(job);
        setSeeker(seeker);
        setResume(resume);
    }

    // Encapsulated Validated Getters and Setters

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = Objects.requireNonNull(job, "Job must not be null.");
    }

    public User getSeeker() {
        return seeker;
    }

    public void setSeeker(User seeker) {
        this.seeker = Objects.requireNonNull(seeker, "Seeker must not be null.");
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = Objects.requireNonNull(resume, "Resume must not be null.");
    }

    public Short getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Short matchScore) {
        if (matchScore != null && (matchScore < 0 || matchScore > 100)) {
            throw new IllegalArgumentException("Match score must be between 0 and 100.");
        }
        this.matchScore = matchScore;
    }

    public AppStatus getStatus() {
        return status;
    }

    public void setStatus(AppStatus status) {
        this.status = Objects.requireNonNull(status, "Application status must not be null.");
    }

    public List<String> getMatchedSkills() {
        return matchedSkills != null ? Collections.unmodifiableList(matchedSkills) : Collections.emptyList();
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills != null ? new ArrayList<>(matchedSkills) : new ArrayList<>();
    }

    public List<String> getMissingSkills() {
        return missingSkills != null ? Collections.unmodifiableList(missingSkills) : Collections.emptyList();
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills != null ? new ArrayList<>(missingSkills) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + getId() +
                ", job=" + (job != null ? job.getId() : null) +
                ", seeker=" + (seeker != null ? seeker.getId() : null) +
                ", matchScore=" + matchScore +
                ", status=" + status +
                '}';
    }
}
