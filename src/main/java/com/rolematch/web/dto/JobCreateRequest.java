package com.rolematch.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record JobCreateRequest(
        @NotBlank(message = "Job title is required")
        @Size(max = 200, message = "Job title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Company name is required")
        @Size(max = 150, message = "Company name must not exceed 150 characters")
        String company,

        @NotNull(message = "Minimum salary is required")
        @DecimalMin(value = "0.00", message = "Minimum salary must be non-negative")
        BigDecimal minSalary,

        @NotNull(message = "Maximum salary is required")
        @DecimalMin(value = "0.00", message = "Maximum salary must be non-negative")
        BigDecimal maxSalary,

        @NotBlank(message = "Job description is required")
        String description,

        List<String> skills,

        String department,

        String location,

        Integer atsThreshold
) {
    public JobCreateRequest(
            String title,
            String company,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            String description,
            List<String> skills
    ) {
        this(
                title,
                company,
                minSalary,
                maxSalary,
                description,
                skills,
                "Engineering & Infrastructure",
                "Remote Node (Global Epoch)",
                75
        );
    }
}
