package com.rolematch.engine.strategy.heuristic;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Heuristic evaluator for professional seniority markers, years of experience,
 * and financial compensation band compatibility.
 */
@Component
public class CompensationExperienceHeuristic {

    private static final Pattern YEARS_PATTERN = Pattern.compile("(?i)(\\d{1,2})\\+?\\s*(?:years?|yrs?)(?:\\s+of)?\\s+(?:experience|exp)");

    private static final Map<String, Integer> SENIORITY_LEVELS = Map.ofEntries(
            Map.entry("intern", 0),
            Map.entry("entry", 1),
            Map.entry("junior", 1),
            Map.entry("associate", 2),
            Map.entry("mid", 3),
            Map.entry("intermediate", 3),
            Map.entry("senior", 4),
            Map.entry("lead", 5),
            Map.entry("staff", 6),
            Map.entry("principal", 7),
            Map.entry("architect", 7),
            Map.entry("director", 8)
    );

    public record HeuristicEvaluation(double score, Map<String, Object> telemetry) {}

    /**
     * Evaluates candidate-job fit using experience level heuristics and compensation sanity.
     *
     * @param resume Candidate resume
     * @param job Target job posting
     * @return Evaluation score in [0.0, 100.0] and diagnostic telemetry
     */
    public HeuristicEvaluation evaluate(Resume resume, Job job) {
        if (resume == null || job == null) {
            return new HeuristicEvaluation(50.0, Map.of("reason", "Null resume or job provided"));
        }

        String resumeText = resume.getExtractedText() != null ? resume.getExtractedText() : "";
        String jobTitle = job.getTitle() != null ? job.getTitle() : "";
        String jobDesc = job.getDescription() != null ? job.getDescription() : "";

        // 1. Seniority alignment
        int jobSeniority = detectSeniority(jobTitle + " " + jobDesc);
        int candidateSeniority = detectSeniority(resumeText);

        double seniorityScore;
        if (jobSeniority == -1) {
            // No strict seniority requirement found in job
            seniorityScore = 80.0;
        } else if (candidateSeniority == -1) {
            // Candidate has no explicit seniority title keywords
            seniorityScore = 65.0;
        } else {
            int delta = Math.abs(jobSeniority - candidateSeniority);
            if (delta == 0) {
                seniorityScore = 100.0;
            } else if (delta == 1) {
                // Adjacent level (e.g., Mid applying to Senior, or Senior to Lead)
                seniorityScore = 85.0;
            } else if (delta == 2) {
                seniorityScore = 65.0;
            } else {
                seniorityScore = Math.max(30.0, 100.0 - (delta * 25.0));
            }
        }

        // 2. Experience Years Extraction
        int candidateMaxYears = extractMaxYears(resumeText);
        int jobRequiredYears = extractMaxYears(jobDesc);

        double experienceScore;
        if (jobRequiredYears <= 0) {
            experienceScore = 80.0;
        } else if (candidateMaxYears <= 0) {
            experienceScore = 60.0;
        } else if (candidateMaxYears >= jobRequiredYears) {
            experienceScore = 100.0;
        } else {
            // Ratio of candidate years to required years
            double ratio = (double) candidateMaxYears / (double) jobRequiredYears;
            experienceScore = Math.clamp(ratio * 100.0, 30.0, 95.0);
        }

        // 3. Compensation sanity check
        double compensationScore = 80.0; // default baseline market sanity
        if (job.getMinSalary() != null && job.getMaxSalary() != null) {
            if (job.getMaxSalary().compareTo(BigDecimal.ZERO) > 0) {
                if (job.getMinSalary().compareTo(job.getMaxSalary()) <= 0) {
                    compensationScore = 90.0;
                }
            }
        }

        // Weighted composite heuristic: 40% Seniority, 40% Experience Years, 20% Compensation
        double compositeScore = (seniorityScore * 0.40) + (experienceScore * 0.40) + (compensationScore * 0.20);
        compositeScore = Math.clamp(compositeScore, 0.0, 100.0);

        Map<String, Object> telemetry = Map.of(
                "jobSeniorityLevel", jobSeniority,
                "candidateSeniorityLevel", candidateSeniority,
                "seniorityScore", seniorityScore,
                "candidateMaxYears", candidateMaxYears,
                "jobRequiredYears", jobRequiredYears,
                "experienceScore", experienceScore,
                "compensationScore", compensationScore,
                "heuristicCompositeScore", compositeScore
        );

        return new HeuristicEvaluation(compositeScore, telemetry);
    }

    private int detectSeniority(String text) {
        if (text == null || text.isBlank()) return -1;
        String lower = text.toLowerCase();
        int highestLevel = -1;

        for (Map.Entry<String, Integer> entry : SENIORITY_LEVELS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                if (entry.getValue() > highestLevel) {
                    highestLevel = entry.getValue();
                }
            }
        }
        return highestLevel;
    }

    private int extractMaxYears(String text) {
        if (text == null || text.isBlank()) return 0;
        Matcher matcher = YEARS_PATTERN.matcher(text);
        int maxYears = 0;
        while (matcher.find()) {
            try {
                int years = Integer.parseInt(matcher.group(1));
                if (years < 45 && years > maxYears) {
                    maxYears = years;
                }
            } catch (NumberFormatException ignored) {}
        }
        return maxYears;
    }
}
