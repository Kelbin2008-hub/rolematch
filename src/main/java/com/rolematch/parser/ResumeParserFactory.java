package com.rolematch.parser;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * Factory pattern implementation dynamically resolving the appropriate AbstractResumeParser
 * strategy based on MIME type detection and filename extension analysis.
 */
@Component
public class ResumeParserFactory {

    private final List<AbstractResumeParser> parsers;

    public ResumeParserFactory(List<AbstractResumeParser> parsers) {
        this.parsers = Objects.requireNonNull(parsers, "Parsers list must not be null");
    }

    /**
     * Resolves the matching concrete parser instance for the provided file.
     *
     * @param file Uploaded resume file
     * @return Concrete AbstractResumeParser instance
     * @throws IllegalArgumentException if the file format is unsupported
     */
    public AbstractResumeParser getParser(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot determine parser for null or empty file.");
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        for (AbstractResumeParser parser : parsers) {
            if (parser.supports(contentType, fileName)) {
                return parser;
            }
        }

        throw new IllegalArgumentException(
                "Unsupported file format. RoleMatch supports PDF (.pdf) and Microsoft Word (.docx) files. Detected: ["
                        + (fileName != null ? fileName : "unknown") + ", contentType: " + contentType + "]"
        );
    }

    /**
     * Convenience processing method delegating to the resolved parser.
     */
    public ParsedDocument parse(MultipartFile file) {
        return getParser(file).process(file);
    }
}
