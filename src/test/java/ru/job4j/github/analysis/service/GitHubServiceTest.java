package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GitHubServiceTest {

    String username;
    private RestTemplate restTemplate;
    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        username = "testuser";
        gitHubService = new GitHubService(restTemplate, username);
    }

    @Test
    void whenFetchRepositoriesThenReturnList() {
        List<Repository> repos = List.of(
                new Repository(1L, "repo1", "url1"),
                new Repository(2L, "repo2", "url2")
        );
        String url = String.format("https://api.github.com/users/%s/repos", username);
        var stringCaptor = ArgumentCaptor.forClass(String.class);
        var httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        var httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<ParameterizedTypeReference<List<Repository>>> parameterizedTypeReferenceCaptor =
                ArgumentCaptor.forClass(ParameterizedTypeReference.class);

        Mockito.when(restTemplate.exchange(
                stringCaptor.capture(),
                httpMethodCaptor.capture(),
                httpEntityArgumentCaptor.capture(),
                parameterizedTypeReferenceCaptor.capture()
        )).thenReturn(ResponseEntity.ok(repos));

        List<Repository> result = gitHubService.fetchRepositories();
        var stringCaptorResult = stringCaptor.getValue();
        var httpMethodCaptorResult = httpMethodCaptor.getValue();
        var httpEntityArgumentCaptorResult = httpEntityArgumentCaptor.getValue();
        var parameterizedTypeReferenceCaptorResult = parameterizedTypeReferenceCaptor.getValue();

        assertThat(result).isEqualTo(repos);
        assertThat(stringCaptorResult).isEqualTo(url);
        assertThat(httpMethodCaptorResult).isEqualTo(HttpMethod.GET);
        assertThat(httpEntityArgumentCaptorResult).isNull();
        assertThat(parameterizedTypeReferenceCaptorResult).isEqualTo(
                new ParameterizedTypeReference<List<Repository>>() {});
    }

    @Test
    void whenFetchCommitsThenReturnList() {
        List<RepositoryCommits> commits = List.of(
                new RepositoryCommits(
                        new RepositoryCommits.CommitDetail(
                                new RepositoryCommits.CommitAuthor("author1", "2024-01-01T00:00:00Z"), "msg1")),
                new RepositoryCommits(
                        new RepositoryCommits.CommitDetail(
                                new RepositoryCommits.CommitAuthor("author2", "2024-01-02T00:00:00Z"), "msg2"))
        );
        String repoName = "repo1";
        String url = String.format("https://api.github.com/repos/%s/%s/commits", username, repoName);
        var stringCaptor = ArgumentCaptor.forClass(String.class);
        var httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        var httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<ParameterizedTypeReference<List<RepositoryCommits>>> parameterizedTypeReferenceCaptor =
                ArgumentCaptor.forClass(ParameterizedTypeReference.class);
        Mockito.when(restTemplate.exchange(
                stringCaptor.capture(),
                httpMethodCaptor.capture(),
                httpEntityArgumentCaptor.capture(),
                parameterizedTypeReferenceCaptor.capture()
        )).thenReturn(ResponseEntity.ok(commits));

        List<RepositoryCommits> result = gitHubService.fetchCommits(repoName);
        var stringCaptorResult = stringCaptor.getValue();
        var httpMethodCaptorResult = httpMethodCaptor.getValue();
        var httpEntityArgumentCaptorResult = httpEntityArgumentCaptor.getValue();
        var parameterizedTypeReferenceCaptorResult = parameterizedTypeReferenceCaptor.getValue();

        assertThat(result).isEqualTo(commits);
        assertThat(stringCaptorResult).isEqualTo(url);
        assertThat(httpMethodCaptorResult).isEqualTo(HttpMethod.GET);
        assertThat(httpEntityArgumentCaptorResult).isNull();
        assertThat(parameterizedTypeReferenceCaptorResult).isEqualTo(
                new ParameterizedTypeReference<List<RepositoryCommits>>() {});
    }

    @Test
    void whenFetchCommitsWithSinceThenReturnList() {
        List<RepositoryCommits> commits = List.of(
                new RepositoryCommits(
                        new RepositoryCommits.CommitDetail(
                                new RepositoryCommits.CommitAuthor("author1", "2024-01-01T00:00:00Z"), "msg1"))
        );
        String repoName = "repo1";
        LocalDateTime since = LocalDateTime.of(2024, 1, 1, 0, 0);
        String url = String.format("https://api.github.com/repos/%s/%s/commits?since=%s", username, repoName, since);
        var stringCaptor = ArgumentCaptor.forClass(String.class);
        var httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        var httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<ParameterizedTypeReference<List<RepositoryCommits>>> parameterizedTypeReferenceCaptor =
                ArgumentCaptor.forClass(ParameterizedTypeReference.class);

        Mockito.when(restTemplate.exchange(
                stringCaptor.capture(),
                httpMethodCaptor.capture(),
                httpEntityArgumentCaptor.capture(),
                parameterizedTypeReferenceCaptor.capture()
        )).thenReturn(ResponseEntity.ok(commits));

        List<RepositoryCommits> result = gitHubService.fetchCommits(repoName, since);
        var stringCaptorResult = stringCaptor.getValue();
        var httpMethodCaptorResult = httpMethodCaptor.getValue();
        var httpEntityArgumentCaptorResult = httpEntityArgumentCaptor.getValue();
        var parameterizedTypeReferenceCaptorResult = parameterizedTypeReferenceCaptor.getValue();

        assertThat(result).isEqualTo(commits);
        assertThat(stringCaptorResult).isEqualTo(url);
        assertThat(httpMethodCaptorResult).isEqualTo(HttpMethod.GET);
        assertThat(httpEntityArgumentCaptorResult).isNull();
        assertThat(parameterizedTypeReferenceCaptorResult).isEqualTo(
                new ParameterizedTypeReference<List<RepositoryCommits>>() {});
    }
}