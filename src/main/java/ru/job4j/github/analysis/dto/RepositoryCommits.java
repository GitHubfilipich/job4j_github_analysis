package ru.job4j.github.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryCommits {
    private CommitDetail commit;

    @Getter
    @AllArgsConstructor
    public static class CommitDetail {
        private CommitAuthor author;
        private String message;
    }

    @Getter
    @AllArgsConstructor
    public static class CommitAuthor {
        private String name;
        private String date;
    }
}
