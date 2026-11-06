package com.rolematch.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Strongly typed representation of the structured JSON response returned by the Gemini 1.5 AI model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiAnalysisResult(
        @JsonProperty("atsScore") int atsScore,
        @JsonProperty("matchedSkills") List<String> matchedSkills,
        @JsonProperty("missingSkills") List<String> missingSkills,
        @JsonProperty("resumeSuggestions") List<String> resumeSuggestions,
        @JsonProperty("technicalQuestions") List<String> technicalQuestions
) implements Serializable {

    public GeminiAnalysisResult {
        matchedSkills = matchedSkills != null ? List.copyOf(matchedSkills) : List.of();
        missingSkills = missingSkills != null ? List.copyOf(missingSkills) : List.of();
        resumeSuggestions = resumeSuggestions != null ? List.copyOf(resumeSuggestions) : List.of();
        technicalQuestions = technicalQuestions != null ? List.copyOf(technicalQuestions) : List.of();
    }

    public static GeminiAnalysisResult fallback(int score, List<String> matched, List<String> missing) {
        return new GeminiAnalysisResult(
                score,
                matched,
                missing,
                List.of(
                        "Quantify achievements with specific business and engineering impact metrics.",
                        "Highlight production experience with scalable distributed systems architecture.",
                        "Clarify ownership of key microservices and CI/CD automation pipelines."
                ),
                List.of(
                        "How do you design database schemas to guarantee idempotency and isolation under high concurrent load?",
                        "Describe your strategy for tuning JVM garbage collection and diagnosing memory leaks in Kubernetes.",
                        "Explain how you implement transactional outbox patterns with PostgreSQL and message brokers."
                )
        );
    }
}
