package com.portfolio.config;

import com.portfolio.model.Project;
import com.portfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;

    @Override
    public void run(String... args) {
        List<Project> projects = List.of(
                Project.builder()
                        .title("FarmChainX: Agricultural Supply Chain Traceability System")
                        .description("A full-stack web application that improves transparency in the agricultural "
                                + "supply chain by showing crop information and stage-wise pricing from farmers "
                                + "to consumers. It maintains crop and inventory details such as crop name, "
                                + "quantity, quality grade, harvest date, location, and the farmer, distributor "
                                + "and retailer prices. It generates QR codes and supports image-based QR "
                                + "scanning, so a consumer can upload a QR code image and instantly see the "
                                + "crop's details. It also displays recorded AI quality scores and verdicts, "
                                + "helping users check the quality of the crop.")
                        .technologies("React.js, Java, Spring Boot, MySQL, REST APIs, QR Code Processing")
                        .githubLink("https://github.com/B-Rishitha15/FARMCHAIN-X")
                        .build(),
                Project.builder()
                        .title("Developer Portfolio Website")
                        .description("My personal portfolio website built with Spring Boot and Thymeleaf. "
                                + "Projects are stored in a MySQL database using Spring Data JPA and rendered "
                                + "dynamically on the page. It includes a contact form with server-side "
                                + "validation and spam protection that emails messages through JavaMailSender, "
                                + "and an interactive Tailwind CSS interface with scroll animations, a typing "
                                + "effect and a skills filter.")
                        .technologies("Java, Spring Boot, Spring Data JPA, Thymeleaf, Tailwind CSS, MySQL, JavaMailSender")
                        .githubLink("https://github.com/B-Rishitha15/portfolio-website")
                        .build()
        );

        for (Project project : projects) {
            if (!projectRepository.existsByTitle(project.getTitle())) {
                projectRepository.save(project);
                log.info("Added project: {}", project.getTitle());
            }
        }
    }
}