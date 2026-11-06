package com.rolematch.web.dto;

import com.rolematch.domain.enums.AppStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequest(
        @NotNull(message = "New application status is required")
        AppStatus status
) {}
