package com.rolematch.web.dto;

import com.rolematch.domain.enums.JobStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record JobViewDto(
        Long id,
        String title,
        String company,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        String description,
        JobStatus status,
        List<String> skills,
        Long recruiterId,
        String recruiterName,
        OffsetDateTime createdAt,
        String department,
        String location,
        Integer atsThreshold
) implements Serializable {

    // Overloaded canonical constructor for backward compatibility
    public JobViewDto(
            Long id,
            String title,
            String company,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            String description,
            JobStatus status,
            List<String> skills,
            Long recruiterId,
            String recruiterName,
            OffsetDateTime createdAt
    ) {
        this(
                id,
                title,
                company,
                minSalary,
                maxSalary,
                description,
                status,
                skills,
                recruiterId,
                recruiterName,
                createdAt,
                "Engineering & Infrastructure",
                "Remote Node (Global Epoch)",
                75
        );
    }
}
