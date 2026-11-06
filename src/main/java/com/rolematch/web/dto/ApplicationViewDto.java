package com.rolematch.web.dto;

import com.rolematch.domain.enums.AppStatus;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

public record ApplicationViewDto(
        Long id,
        Long jobId,
        String jobTitle,
        String company,
        Long seekerId,
        String seekerName,
        String seekerEmail,
        Long resumeId,
        String resumeFileName,
        Short matchScore,
        AppStatus status,
        List<String> matchedSkills,
        List<String> missingSkills,
        OffsetDateTime createdAt
) implements Serializable {

    public String matchDetails() {
        if (matchedSkills != null && !matchedSkills.isEmpty()) {
            return String.join(", ", matchedSkills);
        }
        return "Telemetry: Algorithmic fit evaluated";
    }

    public OffsetDateTime appliedAt() {
        return createdAt;
    }
}
