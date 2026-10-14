package com.portfolio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    /** Comma-separated list, e.g. "Java 21, Spring Boot, PostgreSQL". */
    @Column(nullable = false, length = 500)
    private String technologies;

    @Column(name = "github_link", nullable = false, length = 500)
    private String githubLink;

    @Transient
    public List<String> getTechnologyList() {
        if (technologies == null || technologies.isBlank()) {
            return List.of();
        }
        return Arrays.stream(technologies.split(","))
                .map(String::trim)
                .filter(tech -> !tech.isEmpty())
                .toList();
    }
}