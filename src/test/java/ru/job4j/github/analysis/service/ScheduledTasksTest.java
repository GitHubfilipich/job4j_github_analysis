package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledTasksTest {

    private RepositoryService repositoryService;
    private CommitService commitService;
    private GitHubService gitHubService;
    private ScheduledTasks scheduledTasks;

    @BeforeEach
    void setUp() {
        repositoryService = Mockito.mock(RepositoryService.class);
        commitService = Mockito.mock(CommitService.class);
        gitHubService = Mockito.mock(GitHubService.class);
        scheduledTasks = new ScheduledTasks(repositoryService, commitService, gitHubService);
    }

    @Test
    void whenFetchRepositoriesThenCreateOrUpdateRepository() {
        Repository repo1 = new Repository(1L, "repo1", "url1");
        Repository repo2 = new Repository(2L, "repo2", "url2");
        List<Repository> repos = List.of(repo1, repo2);

        Mockito.when(gitHubService.fetchRepositories()).thenReturn(repos);
        Mockito.when(repositoryService.findByName(repo1.getName())).thenReturn(Optional.empty());
        Mockito.when(repositoryService.findByName(repo2.getName())).thenReturn(
                Optional.of(new Repository(2L, "repo2", "url2")));

        scheduledTasks.fetchRepositories();

        Mockito.verify(repositoryService).create(repo1);
        Mockito.verify(repositoryService).save(repo2);
    }

    @Test
    void whenFetchCommitsAndNoLastCommitThenCreateAllCommits() {
        Repository repo = new Repository(1L, "repo1", "url1");
        List<Repository> repoList = List.of(repo);

        RepositoryCommits rc1 = new RepositoryCommits(
                new RepositoryCommits.CommitDetail(
                        new RepositoryCommits.CommitAuthor("author1", "2024-01-01T00:00:00Z"), "msg1"));
        RepositoryCommits rc2 = new RepositoryCommits(
                new RepositoryCommits.CommitDetail(
                        new RepositoryCommits.CommitAuthor("author2", "2024-01-02T00:00:00Z"), "msg2"));
        List<RepositoryCommits> repositoryCommits = List.of(rc1, rc2);
        var commits = repositoryCommits.stream()
                .map(rc -> getCommitFromDTO(repo, rc))
                .toList();

        Mockito.when(repositoryService.findAll()).thenReturn(repoList);
        Mockito.when(commitService.findLastCommitByRepositoryName(repo.getName())).thenReturn(Optional.empty());
        Mockito.when(gitHubService.fetchCommits(repo.getName())).thenReturn(repositoryCommits);

        scheduledTasks.fetchCommits();

        ArgumentCaptor<Commit> commitCaptor = ArgumentCaptor.forClass(Commit.class);
        Mockito.verify(commitService, Mockito.times(2)).create(commitCaptor.capture());
        List<Commit> createdCommits = commitCaptor.getAllValues();
        assertThat(createdCommits).usingRecursiveComparison().isEqualTo(commits);
    }

    @Test
    void whenFetchCommitsAndLastCommitExistsThenCreateNewCommitsSinceLast() {
        Repository repo = new Repository(1L, "repo1", "url1");
        List<Repository> repoList = List.of(repo);

        Commit lastCommit = new Commit(10L, "oldmsg", "oldauthor", LocalDateTime.of(2024, 1, 1, 0, 0), repo);

        RepositoryCommits rc1 = new RepositoryCommits(
                new RepositoryCommits.CommitDetail(
                        new RepositoryCommits.CommitAuthor("author3", "2024-01-03T00:00:00Z"), "msg3"));
        List<RepositoryCommits> repositoryCommits = List.of(rc1);
        var commit = getCommitFromDTO(repo, rc1);
        var dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        var stringCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.when(repositoryService.findAll()).thenReturn(repoList);
        Mockito.when(commitService.findLastCommitByRepositoryName(repo.getName())).thenReturn(Optional.of(lastCommit));
        Mockito.when(gitHubService.fetchCommits(stringCaptor.capture(), dateCaptor.capture()))
                .thenReturn(repositoryCommits);

        scheduledTasks.fetchCommits();
        var dateCaptorValue = dateCaptor.getValue();
        var stringCaptorValue = stringCaptor.getValue();

        ArgumentCaptor<Commit> commitCaptor = ArgumentCaptor.forClass(Commit.class);
        Mockito.verify(commitService).create(commitCaptor.capture());
        Commit createdCommit = commitCaptor.getValue();
        assertThat(createdCommit).usingRecursiveComparison().isEqualTo(commit);
        assertThat(dateCaptorValue).isEqualTo(lastCommit.getDate());
        assertThat(stringCaptorValue).isEqualTo(repo.getName());
    }

    private Commit getCommitFromDTO(Repository repo, RepositoryCommits repositoryCommits) {
        return new Commit(null, repositoryCommits.getCommit().getMessage(),
                repositoryCommits.getCommit().getAuthor().getName(),
                LocalDateTime.parse(repositoryCommits.getCommit().getAuthor().getDate(),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME), repo);
    }
}