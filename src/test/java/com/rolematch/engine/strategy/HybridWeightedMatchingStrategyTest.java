package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.Skill;
import com.rolematch.domain.entity.User;
import com.rolematch.domain.enums.JobStatus;
import com.rolematch.domain.enums.UserRole;
import com.rolematch.engine.model.MatchResult;
import com.rolematch.engine.strategy.heuristic.CompensationExperienceHeuristic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Hybrid Weighted Matching Strategy (Primary Composite)")
class HybridWeightedMatchingStrategyTest {

    private HybridWeightedMatchingStrategy hybridStrategy;
    private JaccardIndexMatchingStrategy jaccardStrategy;
    private TfIdfCosineMatchingStrategy tfidfStrategy;
    private CompensationExperienceHeuristic heuristicEngine;

    private User recruiter;
    private User seeker;

    @BeforeEach
    void setUp() {
        jaccardStrategy = new JaccardIndexMatchingStrategy();
        tfidfStrategy = new TfIdfCosineMatchingStrategy();
        heuristicEngine = new CompensationExperienceHeuristic();

        hybridStrategy = new HybridWeightedMatchingStrategy(
                jaccardStrategy,
                tfidfStrategy,
                heuristicEngine
        );

        recruiter = new User("Alice Recruiter", "alice@enterprise.com", "hash", UserRole.RECRUITER);
        seeker = new User("Bob Seeker", "bob@candidate.com", "hash", UserRole.SEEKER);
    }

    @Test
    @DisplayName("Should composite 40% Jaccard + 40% TF-IDF Cosine + 20% Heuristic accurately")
    void shouldCompositeWeightsAccurately() {
        Job job = new Job(recruiter, "Senior Java Developer", "GlobalTech",
                new BigDecimal("130000"), new BigDecimal("170000"),
                "Seeking Senior Java Developer with 5 years experience in Spring Boot, PostgreSQL, and REST APIs.",
                JobStatus.OPEN);
        job.setSkills(Set.of(new Skill("Java"), new Skill("Spring Boot"), new Skill("PostgreSQL")));

        Resume resume = new Resume(seeker, "resume.pdf", 3000L,
                "Senior Java Engineer with 7 years of experience building Spring Boot microservices with PostgreSQL and REST APIs.");
        resume.setSkillsSummary(List.of("Java", "Spring Boot", "PostgreSQL"));

        MatchResult compositeResult = hybridStrategy.evaluate(resume, job);

        double jaccardScore = jaccardStrategy.evaluate(resume, job).getOverallScore();
        double tfidfScore = tfidfStrategy.evaluate(resume, job).getOverallScore();
        double heuristicScore = heuristicEngine.evaluate(resume, job).score();

        double expected = (0.40 * jaccardScore) + (0.40 * tfidfScore) + (0.20 * heuristicScore);

        assertThat(compositeResult.getOverallScore()).isCloseTo(expected, within(0.001));
        assertThat(compositeResult.getSubScores()).containsKeys(
                "jaccardScore", "tfidfCosineScore", "heuristicScore", "finalScore"
        );
        assertThat(compositeResult.getRoundedScore()).isEqualTo((short) Math.round(expected));
    }
}
