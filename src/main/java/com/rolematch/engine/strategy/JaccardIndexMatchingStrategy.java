package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.Skill;
import com.rolematch.engine.model.AlgorithmTelemetry;
import com.rolematch.engine.model.MatchResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Strategy implementation based on Set Theory and the Jaccard Similarity Index.
 * Calculates J(A, B) = |A ∩ B| / |A ∪ B| between candidate skills and job requirements.
 */
@Component("jaccardIndexMatchingStrategy")
public class JaccardIndexMatchingStrategy implements MatchingStrategy {

    public static final String STRATEGY_NAME = "JaccardIndexMatchingStrategy";

    @Override
    public MatchResult evaluate(Resume resume, Job job) {
        long startTime = System.nanoTime();

        // Extract and normalize Job skills (Set B)
        Map<String, String> jobSkillMap = new HashMap<>();
        if (job != null && job.getSkills() != null) {
            for (Skill skill : job.getSkills()) {
                if (skill != null && skill.getName() != null && !skill.getName().isBlank()) {
                    jobSkillMap.put(canonicalize(skill.getName()), skill.getName());
                }
            }
        }
        Set<String> setB = jobSkillMap.keySet();

        // Extract and normalize Resume skills (Set A)
        Map<String, String> resumeSkillMap = new HashMap<>();
        if (resume != null) {
            if (resume.getSkillsSummary() != null) {
                for (String skillName : resume.getSkillsSummary()) {
                    if (skillName != null && !skillName.isBlank()) {
                        resumeSkillMap.put(canonicalize(skillName), skillName);
                    }
                }
            }

            // Cross-reference extracted text for any explicit job skills mentioned
            String extractedText = resume.getExtractedText();
            if (extractedText != null && !extractedText.isBlank()) {
                String lowerText = extractedText.toLowerCase();
                for (Map.Entry<String, String> entry : jobSkillMap.entrySet()) {
                    if (lowerText.contains(entry.getKey()) || lowerText.contains(entry.getValue().toLowerCase())) {
                        resumeSkillMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        Set<String> setA = resumeSkillMap.keySet();

        // Edge case handling
        if (setB.isEmpty()) {
            // If the job requires no distinct skills, affinity defaults to 100% or 0% depending on resume presence
            double score = setA.isEmpty() ? 50.0 : 100.0;
            return MatchResult.builder()
                    .overallScore(score)
                    .strategyName(STRATEGY_NAME)
                    .matchedSkills(List.of())
                    .missingSkills(List.of())
                    .subScores(Map.of("jaccardScore", score))
                    .telemetry(new AlgorithmTelemetry(
                            System.nanoTime() - startTime,
                            setA.size(),
                            0.0, 0.0, 0.0,
                            0, 0,
                            Map.of("note", "Job defined with empty skill requirements")
                    ))
                    .build();
        }

        // Set Theory calculations
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);

        int intersectionSize = intersection.size();
        int unionSize = union.size();

        double jaccardIndex = unionSize == 0 ? 0.0 : (double) intersectionSize / (double) unionSize;
        double overallScore = jaccardIndex * 100.0;

        // Categorize matched vs missing skills using original display casing
        List<String> matchedSkills = intersection.stream()
                .map(key -> jobSkillMap.getOrDefault(key, resumeSkillMap.get(key)))
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        List<String> missingSkills = setB.stream()
                .filter(key -> !intersection.contains(key))
                .map(jobSkillMap::get)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        long durationNanos = System.nanoTime() - startTime;

        AlgorithmTelemetry telemetry = new AlgorithmTelemetry(
                durationNanos,
                unionSize,
                0.0,
                setA.size(),
                setB.size(),
                intersectionSize,
                unionSize,
                Map.of(
                        "resumeSkillsCount", setA.size(),
                        "jobSkillsCount", setB.size(),
                        "rawJaccardRatio", jaccardIndex
                )
        );

        return MatchResult.builder()
                .overallScore(overallScore)
                .strategyName(STRATEGY_NAME)
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .subScores(Map.of("jaccardScore", overallScore))
                .telemetry(telemetry)
                .build();
    }

    private String canonicalize(String skill) {
        return skill == null ? "" : skill.trim().toLowerCase().replaceAll("[^a-z0-9+#.-]", "");
    }
}
