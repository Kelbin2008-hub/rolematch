package com.rolematch.engine.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Diagnostic and performance telemetry captured during matching algorithm execution.
 */
public record AlgorithmTelemetry(
        long executionDurationNanos,
        int totalVocabularySize,
        double dotProduct,
        double vectorNormA,
        double vectorNormB,
        int jaccardIntersectionSize,
        int jaccardUnionSize,
        Map<String, Object> diagnosticMetadata
) implements Serializable {

    public static AlgorithmTelemetry empty() {
        return new AlgorithmTelemetry(0L, 0, 0.0, 0.0, 0.0, 0, 0, Map.of());
    }
}
