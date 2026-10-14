package com.portfolio.service;

import com.portfolio.dto.ContactRequest;
import com.portfolio.exception.ContactDeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderAddress;

    @Value("${app.contact.recipient:}")
    private String recipientAddress;

    public void sendContactMessage(ContactRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(senderAddress);
        mail.setTo(recipientAddress);
        mail.setReplyTo(request.getEmail().trim());
        mail.setSubject("[Portfolio] " + singleLine(request.getSubject()));
        mail.setText("""
                New message from your portfolio website

                Name:  %s
                Email: %s

                Message:
                %s
                """.formatted(
                singleLine(request.getName()),
                request.getEmail().trim(),
                request.getMessage().trim()));

        try {
            mailSender.send(mail);
            log.info("Contact message delivered from {}", request.getEmail());
        } catch (MailException ex) {
            log.error("Failed to deliver contact message from {}", request.getEmail(), ex);
            throw new ContactDeliveryException("Unable to deliver contact message", ex);
        }
    }

    /** Prevents header injection by stripping line breaks from header-bound values. */
    private static String singleLine(String value) {
        return value.replaceAll("[\\r\\n]+", " ").trim();
    }
}