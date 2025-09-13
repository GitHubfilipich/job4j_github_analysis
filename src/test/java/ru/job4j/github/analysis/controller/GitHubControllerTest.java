package ru.job4j.github.analysis.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.CommitService;
import ru.job4j.github.analysis.service.RepositoryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class GitHubControllerTest {

    private RepositoryService repositoryService;
    private CommitService commitService;
    private GitHubController gitHubController;

    @BeforeEach
    public void setUp() {
        repositoryService = Mockito.mock(RepositoryService.class);
        commitService = Mockito.mock(CommitService.class);
        gitHubController = new GitHubController(repositoryService, commitService);
    }

    @Test
    void whenGetAllRepositoriesThenGetData() {
        List<Repository> repos = List.of(new Repository(null, "repo1", "url1"), new Repository(null, "repo2", "url2"),
                new Repository(null, "repo3", "url3"));
        Mockito.when(repositoryService.findAll()).thenReturn(repos);

        var result = gitHubController.getAllRepositories();

        assertThat(result).isEqualTo(repos);
    }

    @Test
    void whenGetAllRepositoriesWithoutDataThenGetEmpty() {
        Mockito.when(repositoryService.findAll()).thenReturn(List.of());

        var result = gitHubController.getAllRepositories();

        assertThat(result).isEmpty();
    }

    @Test
    void whenGetCommitsThenGetData() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        List<RepositoryCommits> commits = List.of(new RepositoryCommits(
                new RepositoryCommits.CommitDetail(
                        new RepositoryCommits.CommitAuthor("author1", "2024-01-01T00:00:00Z"), "message1")),
                new RepositoryCommits(
                        new RepositoryCommits.CommitDetail(
                                new RepositoryCommits.CommitAuthor("author2", "2024-01-02T00:00:00Z"), "message2")),
                new RepositoryCommits(
                        new RepositoryCommits.CommitDetail(
                                new RepositoryCommits.CommitAuthor("author3", "2024-01-03T00:00:00Z"), "message3")));
        Mockito.when(commitService.findCommitsByRepositoryName(captor.capture())).thenReturn(commits);
        String repo = "repo1";

        var result = gitHubController.getCommits(repo);
        var captorValue = captor.getValue();

        assertThat(captorValue).isEqualTo(repo);
        assertThat(result).isEqualTo(commits);
    }

    @Test
    void whenGetCommitsWithoutDataThenGetEmpty() {
        Mockito.when(commitService.findCommitsByRepositoryName(Mockito.any(String.class))).thenReturn(List.of());
        String repo = "repo1";

        var result = gitHubController.getCommits(repo);

        assertThat(result).isEmpty();
    }

    @Test
    void whenCreateThenCreateData() {
        ArgumentCaptor<Repository> captor = ArgumentCaptor.forClass(Repository.class);
        var repo = new Repository(null, "repo1", "url1");

        var result = gitHubController.create(repo);

        assertThat(result).isEqualTo(ResponseEntity.noContent().build());
        verify(repositoryService).create(captor.capture());
        var captorValue = captor.getValue();
        assertThat(captorValue).isEqualTo(repo);
    }
}