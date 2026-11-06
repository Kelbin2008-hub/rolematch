package com.rolematch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolematch.domain.entity.Application;
import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.engine.model.MatchResult;
import com.rolematch.engine.strategy.HybridWeightedMatchingStrategy;
import com.rolematch.service.builder.GeminiRequestBuilder;
import com.rolematch.service.model.GeminiAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Enterprise Gemini 1.5 AI service leveraging Spring RestClient and the Builder Pattern
 * to orchestrate LLM-driven ATS candidate analysis and JSONB entity hydration.
 */
@Service
public class GeminiAIService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAIService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final HybridWeightedMatchingStrategy fallbackEngine;
    private final String apiKey;
    private final String apiUrl;

    public GeminiAIService(
            @Value("${gemini.api.key:demo-key}") String apiKey,
            @Value("${gemini.api.url:${gemini.api.endpoint:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}}") String apiUrl,
            ObjectMapper objectMapper,
            HybridWeightedMatchingStrategy fallbackEngine
    ) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper must not be null");
        this.fallbackEngine = Objects.requireNonNull(fallbackEngine, "Fallback engine must not be null");
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Evaluates resume against target job description using Gemini 1.5 API.
     *
     * @param resume Candidate resume
     * @param job Target job requirements
     * @return Deserialized GeminiAnalysisResult
     */
    public GeminiAnalysisResult analyze(Resume resume, Job job) {
        String resumeText = resume != null && resume.getExtractedText() != null ? resume.getExtractedText() : "";
        String jobDesc = job != null && job.getDescription() != null ? job.getDescription() : "";

        // Check if live API key is available or fallback to local algorithmic engine
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("demo-key") || apiKey.startsWith("test-")) {
            log.info("Gemini API key is unconfigured or demo mode; calculating telemetry via HybridWeightedMatchingStrategy");
            return generateAlgorithmicAnalysis(resume, job);
        }

        try {
            // Builder Pattern for request construction
            Map<String, Object> payload = GeminiRequestBuilder.builder()
                    .resumeText(resumeText)
                    .jobDescription(jobDesc)
                    .buildPayload();

            String fullUrl = apiUrl.contains("?key=") ? apiUrl : apiUrl + "?key=" + apiKey;

            String responseBody = restClient.post()
                    .uri(fullUrl)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            return parseGeminiResponse(responseBody, resume, job);
        } catch (Exception ex) {
            log.warn("Gemini API request failed ({}); activating algorithmic fallback strategy", ex.getMessage());
            return generateAlgorithmicAnalysis(resume, job);
        }
    }

    /**
     * Deserializes Gemini API JSON payload and maps it into GeminiAnalysisResult.
     */
    public GeminiAnalysisResult parseGeminiResponse(String responseJson, Resume resume, Job job) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode textNode = root.at("/candidates/0/content/parts/0/text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                throw new IllegalStateException("Gemini response missing candidate parts text node");
            }

            String rawContent = textNode.asText().trim();
            // Clean any potential markdown wrapping
            if (rawContent.startsWith("```json")) {
                rawContent = rawContent.substring(7);
            } else if (rawContent.startsWith("```")) {
                rawContent = rawContent.substring(3);
            }
            if (rawContent.endsWith("```")) {
                rawContent = rawContent.substring(0, rawContent.length() - 3);
            }
            rawContent = rawContent.trim();

            return objectMapper.readValue(rawContent, GeminiAnalysisResult.class);
        } catch (Exception ex) {
            log.error("Failed to deserialize Gemini output: {}. Falling back to algorithmic engine.", ex.getMessage());
            return generateAlgorithmicAnalysis(resume, job);
        }
    }

    /**
     * Hydrates the JSONB fields of Resume and Application entities with Gemini results.
     */
    public void applyAnalysisToEntities(Resume resume, Application application, GeminiAnalysisResult result) {
        if (resume != null) {
            resume.setAtsBaseScore(result.atsScore());
            resume.setSkillsSummary(result.matchedSkills());
            resume.setImprovementSuggestions(result.resumeSuggestions());
            resume.setInterviewQuestions(result.technicalQuestions());
        }

        if (application != null) {
            application.setMatchScore((short) result.atsScore());
            application.setMatchedSkills(result.matchedSkills());
            application.setMissingSkills(result.missingSkills());
        }
    }

    private GeminiAnalysisResult generateAlgorithmicAnalysis(Resume resume, Job job) {
        if (resume == null || job == null) {
            return GeminiAnalysisResult.fallback(70, List.of("Java"), List.of());
        }

        MatchResult matchResult = fallbackEngine.evaluate(resume, job);
        int score = (int) Math.round(matchResult.getOverallScore());
        List<String> matched = matchResult.getMatchedSkills();
        List<String> missing = matchResult.getMissingSkills();

        return GeminiAnalysisResult.fallback(score, matched, missing);
    }
}
