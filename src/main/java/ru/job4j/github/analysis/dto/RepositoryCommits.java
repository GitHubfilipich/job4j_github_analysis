package ru.job4j.github.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RepositoryCommits {
    private String message;
    private String author;
    private LocalDateTime date;
}
