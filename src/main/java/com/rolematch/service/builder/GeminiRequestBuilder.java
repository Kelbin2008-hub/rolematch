package com.rolematch.service.builder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * GoF Builder Pattern implementation constructing the JSON payload structure required
 * by the Google Gemini 1.5 GenerateContent REST endpoint.
 */
public final class GeminiRequestBuilder {

    private String resumeText;
    private String jobDescription;
    private double temperature = 0.2; // Low temperature for deterministic factual extraction

    private GeminiRequestBuilder() {
    }

    public static GeminiRequestBuilder builder() {
        return new GeminiRequestBuilder();
    }

    public GeminiRequestBuilder resumeText(String resumeText) {
        this.resumeText = resumeText;
        return this;
    }

    public GeminiRequestBuilder jobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
        return this;
    }

    public GeminiRequestBuilder temperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    /**
     * Builds the final JSON map structure conforming to Gemini 1.5 specification with schema enforcement.
     */
    public Map<String, Object> buildPayload() {
        Objects.requireNonNull(resumeText, "Resume text must not be null for Gemini evaluation");
        Objects.requireNonNull(jobDescription, "Job description must not be null for Gemini evaluation");

        String prompt = """
                You are an authoritative Master Technical Recruiter and algorithmic ATS evaluation engine.
                Evaluate the candidate's extracted resume text against the provided job specifications.
                
                You must return STRICTLY a single raw JSON object with NO preamble, NO explanations, and NO markdown ticks (no ```json).
                
                Strict JSON Output Schema:
                {
                  "atsScore": <integer between 0 and 100 representing technical alignment>,
                  "matchedSkills": ["skill1", "skill2"],
                  "missingSkills": ["missing1", "missing2"],
                  "resumeSuggestions": ["actionable advice 1", "actionable advice 2", "actionable advice 3"],
                  "technicalQuestions": ["deep technical interview question 1", "deep technical interview question 2", "deep technical interview question 3"]
                }
                
                ---
                TARGET JOB SPECIFICATIONS:
                %s
                
                ---
                CANDIDATE RESUME TEXT:
                %s
                """.formatted(jobDescription, resumeText);

        return Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "responseMimeType", "application/json"
                )
        );
    }
}
