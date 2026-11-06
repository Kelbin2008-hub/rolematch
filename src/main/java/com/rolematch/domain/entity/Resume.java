package com.rolematch.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Enterprise Resume entity encapsulating extracted candidate documentation, file metadata,
 * and AI-derived JSONB analysis (skills, scores, interview prep).
 */
@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_resume_seeker", columnList = "seeker_id")
})
public class Resume extends AuditableEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seeker_id", nullable = false)
    private User seeker;

    @NotBlank
    @Size(max = 255)
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotNull
    @PositiveOrZero
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotBlank
    @Column(name = "extracted_text", columnDefinition = "TEXT", nullable = false)
    private String extractedText;

    @Column(name = "ats_base_score")
    private Integer atsBaseScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills_summary", columnDefinition = "jsonb")
    private List<String> skillsSummary = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "improvement_suggestions", columnDefinition = "jsonb")
    private List<String> improvementSuggestions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "interview_questions", columnDefinition = "jsonb")
    private List<String> interviewQuestions = new ArrayList<>();

    public Resume() {
    }

    public Resume(User seeker, String fileName, Long fileSize, String extractedText) {
        setSeeker(seeker);
        setFileName(fileName);
        setFileSize(fileSize);
        setExtractedText(extractedText);
    }

    // Encapsulated Validated Getters and Setters

    public User getSeeker() {
        return seeker;
    }

    public void setSeeker(User seeker) {
        this.seeker = Objects.requireNonNull(seeker, "Seeker reference must not be null.");
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name must not be blank.");
        }
        this.fileName = fileName.trim();
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        if (fileSize == null || fileSize < 0) {
            throw new IllegalArgumentException("File size must be non-negative.");
        }
        this.fileSize = fileSize;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new IllegalArgumentException("Extracted resume text must not be blank.");
        }
        this.extractedText = extractedText;
    }

    public Integer getAtsBaseScore() {
        return atsBaseScore;
    }

    public void setAtsBaseScore(Integer atsBaseScore) {
        if (atsBaseScore != null && (atsBaseScore < 0 || atsBaseScore > 100)) {
            throw new IllegalArgumentException("ATS base score must be between 0 and 100.");
        }
        this.atsBaseScore = atsBaseScore;
    }

    public List<String> getSkillsSummary() {
        return skillsSummary != null ? Collections.unmodifiableList(skillsSummary) : Collections.emptyList();
    }

    public void setSkillsSummary(List<String> skillsSummary) {
        this.skillsSummary = skillsSummary != null ? new ArrayList<>(skillsSummary) : new ArrayList<>();
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions != null ? Collections.unmodifiableList(improvementSuggestions) : Collections.emptyList();
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions != null ? new ArrayList<>(improvementSuggestions) : new ArrayList<>();
    }

    public List<String> getInterviewQuestions() {
        return interviewQuestions != null ? Collections.unmodifiableList(interviewQuestions) : Collections.emptyList();
    }

    public void setInterviewQuestions(List<String> interviewQuestions) {
        this.interviewQuestions = interviewQuestions != null ? new ArrayList<>(interviewQuestions) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Resume{" +
                "id=" + getId() +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", atsBaseScore=" + atsBaseScore +
                '}';
    }
}
