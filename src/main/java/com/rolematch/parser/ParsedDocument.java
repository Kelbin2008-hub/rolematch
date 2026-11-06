package com.rolematch.parser;

import java.io.Serializable;
import java.util.Objects;

/**
 * Encapsulation of extracted document content and metadata produced by resume parsers.
 */
public record ParsedDocument(
        String fileName,
        long fileSize,
        String extractedText,
        String contentType
) implements Serializable {

    public ParsedDocument {
        Objects.requireNonNull(fileName, "fileName must not be null");
        Objects.requireNonNull(extractedText, "extractedText must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
    }
}
