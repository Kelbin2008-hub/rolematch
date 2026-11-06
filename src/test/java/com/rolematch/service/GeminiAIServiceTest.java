package com.rolematch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rolematch.domain.entity.Application;
import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.engine.strategy.HybridWeightedMatchingStrategy;
import com.rolematch.service.builder.GeminiRequestBuilder;
import com.rolematch.service.model.GeminiAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Gemini 1.5 AI Service & Builder Pattern Tests")
class GeminiAIServiceTest {

    private GeminiAIService geminiAIService;
    private ObjectMapper objectMapper;
    private HybridWeightedMatchingStrategy fallbackEngine;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        fallbackEngine = Mockito.mock(HybridWeightedMatchingStrategy.class);
        geminiAIService = new GeminiAIService("demo-key", "https://mock.gemini.api", objectMapper, fallbackEngine);
    }

    @Test
    @DisplayName("GeminiRequestBuilder should structure strict JSON schema with temperature and prompt")
    void testGeminiRequestBuilderStructure() {
        Map<String, Object> payload = GeminiRequestBuilder.builder()
                .resumeText("Java developer with Spring Boot")
                .jobDescription("Senior Backend Engineer")
                .temperature(0.1)
                .buildPayload();

        assertThat(payload).containsKey("contents");
        assertThat(payload).containsKey("generationConfig");

        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) payload.get("generationConfig");
        assertThat(config.get("responseMimeType")).isEqualTo("application/json");
        assertThat(config.get("temperature")).isEqualTo(0.1);
    }

    @Test
    @DisplayName("Should parse and deserialize raw Gemini JSON response into GeminiAnalysisResult")
    void testParseGeminiResponse() {
        String mockGeminiResponse = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "{\\"atsScore\\": 88, \\"matchedSkills\\": [\\"Java\\", \\"Spring Boot\\", \\"PostgreSQL\\"], \\"missingSkills\\": [\\"Kubernetes\\"], \\"resumeSuggestions\\": [\\"Add distributed caching metrics\\"], \\"technicalQuestions\\": [\\"Explain PostgreSQL MVCC internals\\"]}"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        User seeker = new User("Candidate", "c@dev.io", "pass", UserRole.SEEKER);
        Resume resume = new Resume(seeker, "resume.pdf", 1024L, "Resume content");

        GeminiAnalysisResult result = geminiAIService.parseGeminiResponse(mockGeminiResponse, resume, null);

        assertThat(result.atsScore()).isEqualTo(88);
        assertThat(result.matchedSkills()).containsExactly("Java", "Spring Boot", "PostgreSQL");
        assertThat(result.missingSkills()).containsExactly("Kubernetes");
        assertThat(result.resumeSuggestions()).hasSize(1);
        assertThat(result.technicalQuestions()).hasSize(1);
    }

    @Test
    @DisplayName("Should hydrate Resume and Application JSONB fields with AI analysis output")
    void testApplyAnalysisToEntities() {
        User recruiter = new User("Recruiter", "r@work.io", "h", UserRole.RECRUITER);
        User seeker = new User("Candidate", "c@work.io", "h", UserRole.SEEKER);
        Job job = new Job(recruiter, "Architect", "Tech", new BigDecimal("100000"), new BigDecimal("150000"), "Desc", JobStatus.OPEN);
        Resume resume = new Resume(seeker, "resume.pdf", 2048L, "Resume text");
        Application application = new Application(job, seeker, resume);

        GeminiAnalysisResult result = new GeminiAnalysisResult(
                95,
                List.of("Java", "Spring Data JPA", "PostgreSQL"),
                List.of("AWS Lambda"),
                List.of("Highlight high-concurrency optimizations"),
                List.of("How do you prevent N+1 queries in Spring Data JPA?")
        );

        geminiAIService.applyAnalysisToEntities(resume, application, result);

        assertThat(resume.getAtsBaseScore()).isEqualTo(95);
        assertThat(resume.getSkillsSummary()).containsExactly("Java", "Spring Data JPA", "PostgreSQL");
        assertThat(application.getMatchScore()).isEqualTo((short) 95);
        assertThat(application.getMatchedSkills()).containsExactly("Java", "Spring Data JPA", "PostgreSQL");
        assertThat(application.getMissingSkills()).containsExactly("AWS Lambda");
    }
}
