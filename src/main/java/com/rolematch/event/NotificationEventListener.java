package com.rolematch.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Observer Pattern listener handling CandidateShortlistedEvent asynchronously
 * and dispatching transactional email via Resend API using Spring RestClient.
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestClient restClient;
    private final String resendApiKey;
    private final String fromEmail;

    public NotificationEventListener(
            @Value("${resend.api.key:re_demo_key}") String resendApiKey,
            @Value("${resend.from.email:onboarding@resend.dev}") String fromEmail
    ) {
        this.resendApiKey = resendApiKey;
        this.fromEmail = fromEmail;
        this.restClient = RestClient.builder()
                .baseUrl(RESEND_API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Async
    @EventListener
    public void onCandidateShortlisted(CandidateShortlistedEvent event) {
        log.info("Processing CandidateShortlistedEvent: Seeker [{} <{}>] for Job [{}]",
                event.seekerName(), event.seekerEmail(), event.jobTitle());

        String emailHtml = """
                <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; background: #0f131d; color: #dfe2f1; border-radius: 12px; border: 1px solid rgba(255,255,255,0.1);">
                    <h2 style="color: #6366f1; margin-top: 0;">RoleMatch Candidate Update</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Congratulations! Your profile has been reviewed and <strong style="color: #10b981;">SHORTLISTED</strong> for the role of <strong>%s</strong> at <em>%s</em>.</p>
                    <div style="background: rgba(30, 41, 59, 0.6); padding: 16px; border-radius: 8px; margin: 20px 0; border: 1px solid rgba(255,255,255,0.08);">
                        <p style="margin: 0; font-size: 14px;">Our engineering and recruitment leads will be in touch with next steps regarding technical interview scheduling.</p>
                    </div>
                    <p style="font-size: 13px; color: #94a3b8;">Best regards,<br>The RoleMatch Recruitment Engine</p>
                </div>
                """.formatted(event.seekerName(), event.jobTitle(), event.companyName());

        Map<String, Object> payload = Map.of(
                "from", "RoleMatch <%s>".formatted(fromEmail),
                "to", List.of(event.seekerEmail()),
                "subject", "Congratulations! You've been Shortlisted for " + event.jobTitle(),
                "html", emailHtml
        );

        if (resendApiKey == null || resendApiKey.isBlank() || resendApiKey.startsWith("re_demo") || resendApiKey.equals("demo-resend-key")) {
            log.info("[SIMULATION] Transactional email dispatched to [{}] via Resend. Body: {}", event.seekerEmail(), payload.get("subject"));
            return;
        }

        try {
            String response = restClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + resendApiKey)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("Successfully dispatched shortlist notification email via Resend: {}", response);
        } catch (Exception ex) {
            log.error("Failed to send transactional email via Resend API to [{}]: {}", event.seekerEmail(), ex.getMessage());
            // Graceful non-blocking degradation
        }
    }
}
