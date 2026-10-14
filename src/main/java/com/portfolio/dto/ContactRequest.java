package com.portfolio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Please enter your name.")
    @Size(max = 100, message = "Name must be 100 characters or fewer.")
    private String name;

    @NotBlank(message = "Please enter your email address.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 150, message = "Email must be 150 characters or fewer.")
    private String email;

    @NotBlank(message = "Please enter a subject.")
    @Size(max = 150, message = "Subject must be 150 characters or fewer.")
    private String subject;

    @NotBlank(message = "Please enter a message.")
    @Size(min = 10, max = 3000, message = "Message must be between 10 and 3000 characters.")
    private String message;

    /** Honeypot field: real visitors never see or fill this. Bots usually do. */
    private String website;
}