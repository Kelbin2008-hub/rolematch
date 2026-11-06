package com.rolematch.service;

import com.rolematch.domain.entity.Job;
import com.rolematch.domain.entity.Resume;
import com.rolematch.domain.entity.User;
import com.rolematch.parser.ParsedDocument;
import com.rolematch.parser.ResumeParserFactory;
import com.rolematch.repository.JobRepository;
import com.rolematch.repository.ResumeRepository;
import com.rolematch.repository.UserRepository;
import com.rolematch.service.model.GeminiAnalysisResult;
import com.rolematch.web.dto.ResumeViewDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ResumeParserFactory parserFactory;
    private final GeminiAIService geminiAIService;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            ResumeParserFactory parserFactory,
            GeminiAIService geminiAIService
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.parserFactory = parserFactory;
        this.geminiAIService = geminiAIService;
    }

    @Transactional
    public ResumeViewDto processAndSaveResume(MultipartFile file, Long seekerId, Long targetJobId) {
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new NoSuchElementException("Candidate not found with ID: " + seekerId));

        // 1. Invoke ResumeParserFactory (Template Method & Factory Pattern)
        ParsedDocument parsedDocument = parserFactory.parse(file);

        // 2. Instantiate Resume entity
        Resume resume = new Resume(
                seeker,
                parsedDocument.fileName(),
                parsedDocument.fileSize(),
                parsedDocument.extractedText()
        );

        // 3. Resolve context job for Gemini analysis
        Job targetJob = null;
        if (targetJobId != null) {
            targetJob = jobRepository.findByIdWithSkills(targetJobId).orElse(null);
        }
        if (targetJob == null) {
            // Find any active job to anchor ATS analysis
            List<Job> activeJobs = jobRepository.findAll();
            if (!activeJobs.isEmpty()) {
                targetJob = activeJobs.getFirst();
            }
        }

        // 4. Invoke Gemini 1.5 AI Service (RestClient & Builder Pattern)
        GeminiAnalysisResult aiAnalysis = geminiAIService.analyze(resume, targetJob);

        // 5. Hydrate JSONB fields on Resume
        geminiAIService.applyAnalysisToEntities(resume, null, aiAnalysis);

        Resume saved = resumeRepository.save(resume);
        return toDto(saved);
    }

    public List<ResumeViewDto> getResumesForSeeker(Long seekerId) {
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new NoSuchElementException("Seeker not found with ID: " + seekerId));
        return resumeRepository.findBySeekerOrderByCreatedAtDesc(seeker).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ResumeViewDto> getAllResumes() {
        return resumeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ResumeViewDto> getLatestResumeForSeeker(Long seekerId) {
        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new NoSuchElementException("Seeker not found with ID: " + seekerId));
        return resumeRepository.findFirstBySeekerOrderByCreatedAtDesc(seeker).map(this::toDto);
    }

    public Resume getResumeEntity(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resume not found with ID: " + id));
    }

    public ResumeViewDto toDto(Resume resume) {
        return new ResumeViewDto(
                resume.getId(),
                resume.getSeeker().getId(),
                resume.getSeeker().getName(),
                resume.getFileName(),
                resume.getFileSize(),
                resume.getExtractedText(),
                resume.getAtsBaseScore(),
                resume.getSkillsSummary(),
                resume.getImprovementSuggestions(),
                resume.getInterviewQuestions(),
                resume.getCreatedAt()
        );
    }
}
