package com.portfolio.service;

import com.portfolio.dto.ContactRequest;
import com.portfolio.exception.ContactDeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private static final String RESEND_FROM = "Portfolio Contact <onboarding@resend.dev>";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderAddress;

    @Value("${app.contact.recipient:}")
    private String recipientAddress;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    public void sendContactMessage(ContactRequest request) {
        String subject = "[Portfolio] " + singleLine(request.getSubject());
        String replyTo = request.getEmail().trim();
        String body = """
                New message from your portfolio website

                Name:  %s
                Email: %s

                Message:
                %s
                """.formatted(
                singleLine(request.getName()),
                replyTo,
                request.getMessage().trim());

        if (StringUtils.hasText(resendApiKey)) {
            sendViaResend(subject, replyTo, body);
        } else {
            sendViaSmtp(subject, replyTo, body);
        }
        log.info("Contact message delivered from {}", replyTo);
    }

    /** HTTPS delivery: works on free hosting where SMTP ports are blocked. */
    private void sendViaResend(String subject, String replyTo, String body) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));

        Map<String, Object> payload = Map.of(
                "from", RESEND_FROM,
                "to", List.of(recipientAddress),
                "reply_to", replyTo,
                "subject", subject,
                "text", body);

        try {
            RestClient.builder()
                    .baseUrl("https://api.resend.com")
                    .requestFactory(factory)
                    .build()
                    .post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + resendApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Resend delivery failed: {}", ex.getMessage());
            throw new ContactDeliveryException("Unable to deliver contact message", ex);
        }
    }

    /** SMTP delivery (Gmail): used locally when no Resend key is set. */
    private void sendViaSmtp(String subject, String replyTo, String body) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(senderAddress);
        mail.setTo(recipientAddress);
        mail.setReplyTo(replyTo);
        mail.setSubject(subject);
        mail.setText(body);
        try {
            mailSender.send(mail);
        } catch (MailException ex) {
            log.error("SMTP delivery failed: {}", ex.getMessage());
            throw new ContactDeliveryException("Unable to deliver contact message", ex);
        }
    }

    /** Prevents header injection by stripping line breaks from header-bound values. */
    private static String singleLine(String value) {
        return value.replaceAll("[\\r\\n]+", " ").trim();
    }
}