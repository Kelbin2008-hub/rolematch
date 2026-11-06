package com.rolematch.event;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain event dispatched when a candidate application transitions to the SHORTLISTED stage.
 */
public record CandidateShortlistedEvent(
        String seekerEmail,
        String seekerName,
        String jobTitle,
        String companyName,
        Long applicationId,
        Short matchScore
) implements Serializable {

    public CandidateShortlistedEvent {
        Objects.requireNonNull(seekerEmail, "seekerEmail must not be null");
        Objects.requireNonNull(seekerName, "seekerName must not be null");
        Objects.requireNonNull(jobTitle, "jobTitle must not be null");
    }

    public CandidateShortlistedEvent(String seekerEmail, String seekerName, String jobTitle) {
        this(seekerEmail, seekerName, jobTitle, "RoleMatch Hiring Partner", null, null);
    }
}
