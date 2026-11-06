package com.rolematch.parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Resume Parser Factory & Template Method Tests")
class ResumeParserFactoryTest {

    private ResumeParserFactory factory;
    private PdfResumeParser pdfParser;
    private DocxResumeParser docxParser;

    @BeforeEach
    void setUp() {
        pdfParser = new PdfResumeParser();
        docxParser = new DocxResumeParser();
        factory = new ResumeParserFactory(List.of(pdfParser, docxParser));
    }

    @Test
    @DisplayName("Should extract text from PDF using PdfResumeParser (Apache PDFBox)")
    void shouldExtractTextFromPdf() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Senior Java Architect with Spring Boot and PostgreSQL experience.");
                contentStream.endText();
            }
            doc.save(out);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "candidate_resume.pdf",
                "application/pdf",
                out.toByteArray()
        );

        ParsedDocument parsed = factory.parse(file);

        assertThat(parsed.fileName()).isEqualTo("candidate_resume.pdf");
        assertThat(parsed.extractedText()).contains("Senior Java Architect with Spring Boot and PostgreSQL experience.");
    }

    @Test
    @DisplayName("Should extract text from DOCX using DocxResumeParser (Apache POI)")
    void shouldExtractTextFromDocx() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.createRun().setText("Accomplished Cloud Infrastructure Engineer specializing in Docker and Kubernetes.");
            doc.write(out);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cloud_engineer.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                out.toByteArray()
        );

        ParsedDocument parsed = factory.parse(file);

        assertThat(parsed.fileName()).isEqualTo("cloud_engineer.docx");
        assertThat(parsed.extractedText()).contains("Accomplished Cloud Infrastructure Engineer");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for unsupported formats")
    void shouldRejectUnsupportedFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                "Plain text document".getBytes()
        );

        assertThatThrownBy(() -> factory.parse(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported file format");
    }
}
