package com.rolematch.domain.entity;

import com.rolematch.domain.enums.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

/**
 * Enterprise Job posting entity encapsulating corporate requirements, financial compensation,
 * and associated skill taxonomies.
 */
@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_recruiter", columnList = "recruiter_id"),
        @Index(name = "idx_job_status", columnList = "status"),
        @Index(name = "idx_job_company", columnList = "company")
})
public class Job extends AuditableEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 150)
    @Column(name = "company", nullable = false, length = 150)
    private String company;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(name = "min_salary", nullable = false, precision = 14, scale = 2)
    private BigDecimal minSalary;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(name = "max_salary", nullable = false, precision = 14, scale = 2)
    private BigDecimal maxSalary;

    @NotBlank
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private JobStatus status = JobStatus.PENDING;

    @Size(max = 100)
    @Column(name = "department", length = 100)
    private String department = "Engineering & Infrastructure";

    @Size(max = 150)
    @Column(name = "location", length = 150)
    private String location = "Remote Node (Global Epoch)";

    @Column(name = "ats_threshold")
    private Integer atsThreshold = 75;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id", referencedColumnName = "id")
    )
    private Set<Skill> skills = new HashSet<>();

    public Job() {
        this.status = JobStatus.PENDING;
    }

    public Job(User recruiter, String title, String company, BigDecimal minSalary, BigDecimal maxSalary, String description, JobStatus status) {
        this(recruiter, title, company, minSalary, maxSalary, description, status, "Engineering & Infrastructure", "Remote Node (Global Epoch)", 75);
    }

    public Job(User recruiter, String title, String company, BigDecimal minSalary, BigDecimal maxSalary, String description, JobStatus status, String department, String location, Integer atsThreshold) {
        this();
        setRecruiter(recruiter);
        setTitle(title);
        setCompany(company);
        setCompensation(minSalary, maxSalary);
        setDescription(description);
        setStatus(status != null ? status : JobStatus.PENDING);
        setDepartment(department != null && !department.isBlank() ? department : "Engineering & Infrastructure");
        setLocation(location != null && !location.isBlank() ? location : "Remote Node (Global Epoch)");
        setAtsThreshold(atsThreshold != null ? atsThreshold : 75);
    }

    // Encapsulated Validated Getters and Setters

    public User getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(User recruiter) {
        this.recruiter = Objects.requireNonNull(recruiter, "Recruiter reference must not be null.");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job title must not be blank.");
        }
        this.title = title.trim();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        if (company == null || company.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name must not be blank.");
        }
        this.company = company.trim();
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setCompensation(BigDecimal minSalary, BigDecimal maxSalary) {
        Objects.requireNonNull(minSalary, "Minimum salary must not be null.");
        Objects.requireNonNull(maxSalary, "Maximum salary must not be null.");
        if (minSalary.compareTo(BigDecimal.ZERO) < 0 || maxSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salaries must be non-negative.");
        }
        if (minSalary.compareTo(maxSalary) > 0) {
            throw new IllegalArgumentException("Minimum salary (" + minSalary + ") cannot exceed maximum salary (" + maxSalary + ").");
        }
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        Objects.requireNonNull(minSalary, "Minimum salary must not be null.");
        if (this.maxSalary != null && minSalary.compareTo(this.maxSalary) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot exceed maximum salary.");
        }
        this.minSalary = minSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        Objects.requireNonNull(maxSalary, "Maximum salary must not be null.");
        if (this.minSalary != null && maxSalary.compareTo(this.minSalary) < 0) {
            throw new IllegalArgumentException("Maximum salary cannot be less than minimum salary.");
        }
        this.maxSalary = maxSalary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Job description must not be blank.");
        }
        this.description = description.trim();
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = Objects.requireNonNull(status, "Job status must not be null.");
    }

    public Set<Skill> getSkills() {
        return Collections.unmodifiableSet(skills);
    }

    public void setSkills(Set<Skill> newSkills) {
        this.skills.clear();
        if (newSkills != null) {
            this.skills.addAll(newSkills);
        }
    }

    public void addSkill(Skill skill) {
        if (skill != null) {
            this.skills.add(skill);
        }
    }

    public void removeSkill(Skill skill) {
        if (skill != null) {
            this.skills.remove(skill);
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null && !department.isBlank() ? department.trim() : "Engineering & Infrastructure";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null && !location.isBlank() ? location.trim() : "Remote Node (Global Epoch)";
    }

    public Integer getAtsThreshold() {
        return atsThreshold;
    }

    public void setAtsThreshold(Integer atsThreshold) {
        this.atsThreshold = atsThreshold != null ? Math.clamp(atsThreshold, 0, 100) : 75;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", minSalary=" + minSalary +
                ", maxSalary=" + maxSalary +
                ", status=" + status +
                '}';
    }
}
