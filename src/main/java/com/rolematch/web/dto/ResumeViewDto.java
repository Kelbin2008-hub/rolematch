package com.rolematch.web.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

public record ResumeViewDto(
        Long id,
        Long seekerId,
        String seekerName,
        String fileName,
        long fileSize,
        String extractedText,
        Integer atsBaseScore,
        List<String> skillsSummary,
        List<String> improvementSuggestions,
        List<String> interviewQuestions,
        OffsetDateTime createdAt
) implements Serializable {}
