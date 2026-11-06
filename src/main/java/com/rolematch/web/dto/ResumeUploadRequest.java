package com.rolematch.web.dto;

import org.springframework.web.multipart.MultipartFile;

public record ResumeUploadRequest(
        MultipartFile file,
        Long jobId
) {}
