package com.rolematch.parser;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

/**
 * Abstract resume parser implementing the GoF Template Method Pattern.
 * Defines the fixed skeleton of file validation, text extraction, sanitization,
 * and metadata encapsulation.
 */
public abstract class AbstractResumeParser {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    /**
     * Template Method orchestrating document ingestion and text extraction.
     * Declared final to prevent subclasses from altering the invariant workflow.
     *
     * @param file Multipart resume upload
     * @return Sanitized ParsedDocument
     */
    public final ParsedDocument process(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String safeFileName = (originalFilename != null && !originalFilename.isBlank())
                ? originalFilename.trim()
                : "uploaded_resume";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        long fileSize = file.getSize();

        String rawText;
        try (InputStream inputStream = file.getInputStream()) {
            rawText = extractRawText(inputStream);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse document [" + safeFileName + "]: " + ex.getMessage(), ex);
        }

        String sanitizedText = sanitizeText(rawText);
        if (sanitizedText.isBlank()) {
            throw new IllegalArgumentException("The uploaded file [" + safeFileName + "] contains no readable text content.");
        }

        return new ParsedDocument(safeFileName, fileSize, sanitizedText, contentType);
    }

    /**
     * Primitive hook method implemented by concrete parser subclasses (e.g. PDFBox, POI).
     *
     * @param inputStream Open stream to the document bytes
     * @return Raw extracted text
     * @throws Exception If document format is corrupted or parsing fails
     */
    protected abstract String extractRawText(InputStream inputStream) throws Exception;

    /**
     * Contract for MIME-type and extension evaluation in factory routing.
     */
    public abstract boolean supports(String contentType, String fileName);

    protected void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded resume file must not be empty or null.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds maximum permitted threshold of 10MB.");
        }
    }

    protected String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        // Normalize line breaks and remove null characters or erratic control sequences
        String cleaned = text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");

        // Collapse 3 or more consecutive newlines into 2
        cleaned = cleaned.replaceAll("\n{3,}", "\n\n");
        return cleaned.trim();
    }
}
