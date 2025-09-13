package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Service
public class GitHubService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${github.username}")
    private String username;

    public List<Repository> fetchRepositories() {
        String url = String.format("https://api.github.com/users/%s/repos", username);
        ResponseEntity<List<Repository>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Repository>>() {});
        return response.getBody();
    }

    public List<RepositoryCommits> fetchCommits(String repoName) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits", username, repoName);
        ResponseEntity<List<RepositoryCommits>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RepositoryCommits>>() {});
        return response.getBody();
    }

    public List<RepositoryCommits> fetchCommits(String repoName, LocalDateTime since) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits?since=%s", username, repoName, since);
        ResponseEntity<List<RepositoryCommits>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RepositoryCommits>>() {});
        return response.getBody();
    }
}