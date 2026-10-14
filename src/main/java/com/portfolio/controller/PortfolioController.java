package com.portfolio.controller;

import com.portfolio.dto.ContactRequest;
import com.portfolio.exception.ContactDeliveryException;
import com.portfolio.service.ContactService;
import com.portfolio.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private static final String CONTACT_BINDING_KEY =
            "org.springframework.validation.BindingResult.contactForm";

    private final ProjectService projectService;
    private final ContactService contactService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("projects", projectService.findAllProjects());
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactRequest());
        }
        return "index";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactRequest contactForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        // Honeypot: bots fill the hidden field, so pretend success and drop it
        if (StringUtils.hasText(contactForm.getWebsite())) {
            log.warn("Honeypot field filled; submission discarded.");
            redirectAttributes.addFlashAttribute("successMessage", "Thanks for reaching out!");
            return "redirect:/#contact";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(CONTACT_BINDING_KEY, bindingResult);
            redirectAttributes.addFlashAttribute("contactForm", contactForm);
            return "redirect:/#contact";
        }

        try {
            contactService.sendContactMessage(contactForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thanks for reaching out! Your message has been sent and I'll reply soon.");
        } catch (ContactDeliveryException ex) {
            redirectAttributes.addFlashAttribute("contactForm", contactForm);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Sorry, your message could not be sent right now. Please email me directly at rishibatchu15@gmail.com.");
        }
        return "redirect:/#contact";
    }
}