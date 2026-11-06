package com.rolematch.engine.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable encapsulation of algorithmic evaluation outcomes across set theory,
 * vector space cosine similarity, and composite heuristic scoring.
 */
public final class MatchResult implements Serializable {

    private final double overallScore;
    private final short roundedScore;
    private final String strategyName;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final Map<String, Double> subScores;
    private final AlgorithmTelemetry telemetry;
    private final OffsetDateTime evaluatedAt;

    private MatchResult(Builder builder) {
        this.overallScore = builder.overallScore;
        this.roundedScore = (short) Math.round(builder.overallScore);
        this.strategyName = Objects.requireNonNull(builder.strategyName, "Strategy name must not be null");
        this.matchedSkills = builder.matchedSkills != null ? List.copyOf(builder.matchedSkills) : List.of();
        this.missingSkills = builder.missingSkills != null ? List.copyOf(builder.missingSkills) : List.of();
        this.subScores = builder.subScores != null ? Map.copyOf(builder.subScores) : Map.of();
        this.telemetry = builder.telemetry != null ? builder.telemetry : AlgorithmTelemetry.empty();
        this.evaluatedAt = builder.evaluatedAt != null ? builder.evaluatedAt : OffsetDateTime.now(ZoneOffset.UTC);
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getOverallScore() {
        return overallScore;
    }

    public short getRoundedScore() {
        return roundedScore;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public List<String> getMatchedSkills() {
        return Collections.unmodifiableList(matchedSkills);
    }

    public List<String> getMissingSkills() {
        return Collections.unmodifiableList(missingSkills);
    }

    public Map<String, Double> getSubScores() {
        return Collections.unmodifiableMap(subScores);
    }

    public AlgorithmTelemetry getTelemetry() {
        return telemetry;
    }

    public OffsetDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "overallScore=" + overallScore +
                ", roundedScore=" + roundedScore +
                ", strategyName='" + strategyName + '\'' +
                ", matchedSkills=" + matchedSkills +
                ", missingSkills=" + missingSkills +
                ", subScores=" + subScores +
                '}';
    }

    public static final class Builder {
        private double overallScore;
        private String strategyName;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private Map<String, Double> subScores;
        private AlgorithmTelemetry telemetry;
        private OffsetDateTime evaluatedAt;

        private Builder() {}

        public Builder overallScore(double overallScore) {
            this.overallScore = Math.clamp(overallScore, 0.0, 100.0);
            return this;
        }

        public Builder strategyName(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        public Builder matchedSkills(List<String> matchedSkills) {
            this.matchedSkills = matchedSkills;
            return this;
        }

        public Builder missingSkills(List<String> missingSkills) {
            this.missingSkills = missingSkills;
            return this;
        }

        public Builder subScores(Map<String, Double> subScores) {
            this.subScores = subScores;
            return this;
        }

        public Builder telemetry(AlgorithmTelemetry telemetry) {
            this.telemetry = telemetry;
            return this;
        }

        public Builder evaluatedAt(OffsetDateTime evaluatedAt) {
            this.evaluatedAt = evaluatedAt;
            return this;
        }

        public MatchResult build() {
            return new MatchResult(this);
        }
    }
}
