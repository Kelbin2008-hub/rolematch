package com.rolematch.engine.strategy;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.engine.model.MatchResult;

/**
 * Strategy interface (GoF Strategy Pattern) defining the contract for algorithmic
 * job-candidate compatibility evaluation.
 */
@FunctionalInterface
public interface MatchingStrategy {

    /**
     * Evaluates compatibility between candidate resume content and corporate job requirements.
     *
     * @param resume Candidate resume entity containing extracted text and parsed skills
     * @param job Job posting entity containing descriptions, required skills, and compensation bands
     * @return Complete MatchResult containing calculated score (0-100), telemetry, and skill differentials
     */
    MatchResult evaluate(Resume resume, Job job);
}
