package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ScheduledTasks {
    @Autowired
    private final RepositoryService repositoryService;
    @Autowired
    private final CommitService commitService;
    @Autowired
    private final GitHubService gitHubService;

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchRepositories() {
        gitHubService.fetchRepositories()
                .forEach(repository -> {
                            Optional<Repository> existingRepo = repositoryService.findByName(repository.getName());
                            if (existingRepo.isEmpty()) {
                                repositoryService.create(repository);
                            } else {
                                repository.setId(existingRepo.get().getId());
                                repositoryService.save(repository);
                            }
                        }
                );
    }

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() {
        repositoryService.findAll().forEach(repo -> {
            Optional<Commit> lastCommit = commitService.findLastCommitByRepositoryName(repo.getName());
            if (lastCommit.isEmpty()) {
                gitHubService.fetchCommits(repo.getName())
                        .forEach(repositoryCommits -> {
                            Commit commit = getCommitFromDTO(repo, repositoryCommits);
                            commitService.create(commit);
                        });
            } else {
                LocalDateTime since = lastCommit.get().getDate();
                gitHubService.fetchCommits(repo.getName(), since)
                        .forEach(repositoryCommits -> {
                            Commit commit = getCommitFromDTO(repo, repositoryCommits);
                            commitService.create(commit);
                        });
            }
        });
    }

    private Commit getCommitFromDTO(Repository repo, RepositoryCommits repositoryCommits) {
        return new Commit(null, repositoryCommits.getCommit().getMessage(),
                repositoryCommits.getCommit().getAuthor().getName(),
                LocalDateTime.parse(repositoryCommits.getCommit().getAuthor().getDate(),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME), repo);
    }
}
