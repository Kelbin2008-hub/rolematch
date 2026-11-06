package com.rolematch.parser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Concrete parser for Portable Document Format (.pdf) files utilizing Apache PDFBox 3.x.
 */
@Component
public class PdfResumeParser extends AbstractResumeParser {

    @Override
    protected String extractRawText(InputStream inputStream) throws Exception {
        byte[] bytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    @Override
    public boolean supports(String contentType, String fileName) {
        if (contentType != null && (contentType.equalsIgnoreCase("application/pdf")
                || contentType.equalsIgnoreCase("application/x-pdf"))) {
            return true;
        }
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }
}
