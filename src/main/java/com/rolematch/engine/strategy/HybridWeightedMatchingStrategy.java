package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.engine.model.AlgorithmTelemetry;
import com.rolematch.engine.model.MatchResult;
import com.rolematch.engine.strategy.heuristic.CompensationExperienceHeuristic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Composite matching strategy (GoF Composite / Strategy Pattern).
 * Primary injectable engine compositing Jaccard Set Theory (40%),
 * TF-IDF Cosine Vector Similarity (40%), and Seniority/Compensation Heuristics (20%).
 */
@Primary
@Component("hybridWeightedMatchingStrategy")
public class HybridWeightedMatchingStrategy implements MatchingStrategy {

    public static final String STRATEGY_NAME = "HybridWeightedMatchingStrategy";
    public static final double JACCARD_WEIGHT = 0.40;
    public static final double TFIDF_COSINE_WEIGHT = 0.40;
    public static final double HEURISTIC_WEIGHT = 0.20;

    private final MatchingStrategy jaccardStrategy;
    private final MatchingStrategy tfidfCosineStrategy;
    private final CompensationExperienceHeuristic heuristicEngine;

    public HybridWeightedMatchingStrategy(
            @Qualifier("jaccardIndexMatchingStrategy") MatchingStrategy jaccardStrategy,
            @Qualifier("tfidfCosineMatchingStrategy") MatchingStrategy tfidfCosineStrategy,
            CompensationExperienceHeuristic heuristicEngine
    ) {
        this.jaccardStrategy = Objects.requireNonNull(jaccardStrategy, "Jaccard strategy must not be null");
        this.tfidfCosineStrategy = Objects.requireNonNull(tfidfCosineStrategy, "TF-IDF strategy must not be null");
        this.heuristicEngine = Objects.requireNonNull(heuristicEngine, "Heuristic engine must not be null");
    }

    @Override
    public MatchResult evaluate(Resume resume, Job job) {
        long startTime = System.nanoTime();

        // 1. Evaluate Jaccard Index (Set Theory on Skills) - 40%
        MatchResult jaccardResult = jaccardStrategy.evaluate(resume, job);
        double jaccardScore = jaccardResult.getOverallScore();

        // 2. Evaluate TF-IDF Cosine Similarity (Vector NLP on text) - 40%
        MatchResult tfidfResult = tfidfCosineStrategy.evaluate(resume, job);
        double tfidfScore = tfidfResult.getOverallScore();

        // 3. Evaluate Experience and Compensation Heuristics - 20%
        CompensationExperienceHeuristic.HeuristicEvaluation heuristicResult = heuristicEngine.evaluate(resume, job);
        double heuristicScore = heuristicResult.score();

        // 4. Composite Weighted Score
        double compositeScore = (JACCARD_WEIGHT * jaccardScore)
                + (TFIDF_COSINE_WEIGHT * tfidfScore)
                + (HEURISTIC_WEIGHT * heuristicScore);

        compositeScore = Math.clamp(compositeScore, 0.0, 100.0);

        // Sub-score breakdown
        Map<String, Double> subScores = Map.of(
                "jaccardScore", jaccardScore,
                "jaccardWeighted", jaccardScore * JACCARD_WEIGHT,
                "tfidfCosineScore", tfidfScore,
                "tfidfCosineWeighted", tfidfScore * TFIDF_COSINE_WEIGHT,
                "heuristicScore", heuristicScore,
                "heuristicWeighted", heuristicScore * HEURISTIC_WEIGHT,
                "finalScore", compositeScore
        );

        long durationNanos = System.nanoTime() - startTime;

        Map<String, Object> diagnosticData = new HashMap<>();
        diagnosticData.put("jaccardTelemetry", jaccardResult.getTelemetry());
        diagnosticData.put("tfidfTelemetry", tfidfResult.getTelemetry());
        diagnosticData.put("heuristicTelemetry", heuristicResult.telemetry());
        diagnosticData.put("weights", Map.of("jaccard", JACCARD_WEIGHT, "tfidfCosine", TFIDF_COSINE_WEIGHT, "heuristic", HEURISTIC_WEIGHT));

        AlgorithmTelemetry compositeTelemetry = new AlgorithmTelemetry(
                durationNanos,
                tfidfResult.getTelemetry().totalVocabularySize(),
                tfidfResult.getTelemetry().dotProduct(),
                tfidfResult.getTelemetry().vectorNormA(),
                tfidfResult.getTelemetry().vectorNormB(),
                jaccardResult.getTelemetry().jaccardIntersectionSize(),
                jaccardResult.getTelemetry().jaccardUnionSize(),
                diagnosticData
        );

        return MatchResult.builder()
                .overallScore(compositeScore)
                .strategyName(STRATEGY_NAME)
                .matchedSkills(jaccardResult.getMatchedSkills())
                .missingSkills(jaccardResult.getMissingSkills())
                .subScores(subScores)
                .telemetry(compositeTelemetry)
                .build();
    }
}
