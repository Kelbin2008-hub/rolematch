package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.engine.model.AlgorithmTelemetry;
import com.rolematch.engine.model.MatchResult;
import com.rolematch.engine.nlp.TextTokenizer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Strategy implementation leveraging Natural Language Processing and Linear Algebra.
 * Constructs TF-IDF (Term Frequency - Inverse Document Frequency) vectors over candidate
 * resume and job description texts, computing mathematical Cosine Similarity:
 *
 *               u · v
 *   cos(θ) = -----------
 *            ||u|| ||v||
 */
@Component("tfidfCosineMatchingStrategy")
public class TfIdfCosineMatchingStrategy implements MatchingStrategy {

    public static final String STRATEGY_NAME = "TfIdfCosineMatchingStrategy";

    @Override
    public MatchResult evaluate(Resume resume, Job job) {
        long startTime = System.nanoTime();

        String resumeText = (resume != null && resume.getExtractedText() != null) ? resume.getExtractedText() : "";
        String jobText = (job != null && job.getDescription() != null) ? job.getDescription() : "";

        List<String> resumeTokens = TextTokenizer.tokenize(resumeText);
        List<String> jobTokens = TextTokenizer.tokenize(jobText);

        if (resumeTokens.isEmpty() || jobTokens.isEmpty()) {
            return MatchResult.builder()
                    .overallScore(0.0)
                    .strategyName(STRATEGY_NAME)
                    .matchedSkills(List.of())
                    .missingSkills(List.of())
                    .subScores(Map.of("tfidfCosineScore", 0.0))
                    .telemetry(new AlgorithmTelemetry(
                            System.nanoTime() - startTime,
                            0, 0.0, 0.0, 0.0, 0, 0,
                            Map.of("error", "One or both text corpora yielded 0 valid tokens")
                    ))
                    .build();
        }

        // 1. Calculate Term Frequencies (TF)
        Map<String, Long> resumeTermCounts = resumeTokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<String, Long> jobTermCounts = jobTokens.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        double totalResumeTokens = resumeTokens.size();
        double totalJobTokens = jobTokens.size();

        Map<String, Double> resumeTf = new HashMap<>();
        resumeTermCounts.forEach((term, count) -> resumeTf.put(term, count / totalResumeTokens));

        Map<String, Double> jobTf = new HashMap<>();
        jobTermCounts.forEach((term, count) -> jobTf.put(term, count / totalJobTokens));

        // 2. Build Vocabulary and Document Frequencies (DF)
        Set<String> vocabulary = new HashSet<>(resumeTf.keySet());
        vocabulary.addAll(jobTf.keySet());

        int corpusSize = 2; // [resumeDocument, jobDocument]
        Map<String, Double> idfMap = new HashMap<>(vocabulary.size());

        for (String term : vocabulary) {
            int df = 0;
            if (resumeTf.containsKey(term)) df++;
            if (jobTf.containsKey(term)) df++;

            // Smooth standard IDF formula: ln((1 + |D|) / (1 + df)) + 1.0
            double idf = Math.log((1.0 + corpusSize) / (1.0 + df)) + 1.0;
            idfMap.put(term, idf);
        }

        // 3. Compute TF-IDF Vectors
        Map<String, Double> resumeVector = new HashMap<>();
        resumeTf.forEach((term, tf) -> resumeVector.put(term, tf * idfMap.get(term)));

        Map<String, Double> jobVector = new HashMap<>();
        jobTf.forEach((term, tf) -> jobVector.put(term, tf * idfMap.get(term)));

        // 4. Linear Algebra: Vector Dot Product (u · v)
        double dotProduct = 0.0;
        Map<String, Double> termContribution = new HashMap<>();

        for (Map.Entry<String, Double> entry : resumeVector.entrySet()) {
            String term = entry.getKey();
            Double jobWeight = jobVector.get(term);
            if (jobWeight != null) {
                double product = entry.getValue() * jobWeight;
                dotProduct += product;
                termContribution.put(term, product);
            }
        }

        // 5. Euclidean L2 Norms: ||u|| and ||v||
        double normASquared = 0.0;
        for (double val : resumeVector.values()) {
            normASquared += val * val;
        }
        double normA = Math.sqrt(normASquared);

        double normBSquared = 0.0;
        for (double val : jobVector.values()) {
            normBSquared += val * val;
        }
        double normB = Math.sqrt(normBSquared);

        // 6. Cosine Similarity Calculation
        double cosineSimilarity = 0.0;
        if (normA > 0.0 && normB > 0.0) {
            cosineSimilarity = dotProduct / (normA * normB);
        }
        cosineSimilarity = Math.clamp(cosineSimilarity, 0.0, 1.0);
        double overallScore = cosineSimilarity * 100.0;

        // Top contributing shared terms
        List<String> topSharedTerms = termContribution.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        long durationNanos = System.nanoTime() - startTime;

        AlgorithmTelemetry telemetry = new AlgorithmTelemetry(
                durationNanos,
                vocabulary.size(),
                dotProduct,
                normA,
                normB,
                termContribution.size(),
                vocabulary.size(),
                Map.of(
                        "cosineSimilarity", cosineSimilarity,
                        "resumeTokensCount", resumeTokens.size(),
                        "jobTokensCount", jobTokens.size(),
                        "sharedTermsCount", termContribution.size(),
                        "topSharedTerms", topSharedTerms
                )
        );

        return MatchResult.builder()
                .overallScore(overallScore)
                .strategyName(STRATEGY_NAME)
                .matchedSkills(topSharedTerms)
                .missingSkills(List.of())
                .subScores(Map.of("tfidfCosineScore", overallScore))
                .telemetry(telemetry)
                .build();
    }
}
