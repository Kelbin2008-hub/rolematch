package com.rolematch.parser;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Concrete parser for Microsoft Word (.docx) documents utilizing Apache POI OOXML.
 */
@Component
public class DocxResumeParser extends AbstractResumeParser {

    @Override
    protected String extractRawText(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    @Override
    public boolean supports(String contentType, String fileName) {
        if (contentType != null && (
                contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || contentType.equalsIgnoreCase("application/msword")
                || contentType.equalsIgnoreCase("application/x-docx")
        )) {
            return true;
        }
        return fileName != null && (fileName.toLowerCase().endsWith(".docx") || fileName.toLowerCase().endsWith(".doc"));
    }
}
