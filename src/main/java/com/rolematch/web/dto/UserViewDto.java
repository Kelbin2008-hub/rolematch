package com.rolematch.web.dto;

import com.rolematch.domain.enums.UserRole;
import java.io.Serializable;
import java.util.UUID;

public record UserViewDto(
        Long id,
        UUID uuid,
        String name,
        String email,
        UserRole role,
        Boolean active
) implements Serializable {}
