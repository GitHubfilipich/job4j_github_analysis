package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.CommitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class CommitServiceTest {

    private CommitRepository commitRepository;
    private CommitService commitService;

    @BeforeEach
    void setUp() {
        commitRepository = Mockito.mock(CommitRepository.class);
        commitService = new CommitService(commitRepository);
    }

    @Test
    void whenCreateThenRepositorySaveCalled() {
        Commit commit = new Commit(1L, "msg", "author", LocalDateTime.now(), new Repository(null, "repo", "url"));

        commitService.create(commit);

        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(commitRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(commit);
    }

    @Test
    void whenFindCommitsByRepositoryNameThenReturnDTOList() {
        Repository repo = new Repository(1L, "repo", "url");
        List<Commit> commits = List.of(new Commit(1L, "msg1", "author1", LocalDateTime.of(2024,1,1,12,0), repo),
                new Commit(2L, "msg2", "author2", LocalDateTime.of(2024,1,2,13,0), repo));
        var captor = ArgumentCaptor.forClass(String.class);
        var repoName = "repo1";
        Mockito.when(commitRepository.findByRepository_Name(captor.capture())).thenReturn(commits);
        var repositoryCommits = commits.stream()
                .map(commit -> {
                    return new RepositoryCommits(
                            new RepositoryCommits.CommitDetail(
                                    new RepositoryCommits.CommitAuthor(
                                            commit.getAuthor(),
                                            commit.getDate().toString()
                                    ),
                                    commit.getMessage()
                            )
                    );
                })
                .toList();

        var result = commitService.findCommitsByRepositoryName(repoName);
        var captorValue = captor.getValue();

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(repositoryCommits);
        assertThat(captorValue).isEqualTo(repoName);
    }

    @Test
    void whenFindCommitsByRepositoryNameWithNoCommitsThenReturnEmptyList() {
        Mockito.when(commitRepository.findByRepository_Name(Mockito.any(String.class))).thenReturn(List.of());

        List<RepositoryCommits> result = commitService.findCommitsByRepositoryName("repo1");

        assertThat(result).isEmpty();
    }

    @Test
    void whenFindLastCommitByRepositoryNameThenReturnOptionalCommit() {
        var repoName = "repo1";
        Commit commit = new Commit(1L, "msg", "author", LocalDateTime.of(2024,1,3,14,0),
                new Repository( null, repoName, "url"));
        var captor = ArgumentCaptor.forClass(String.class);
        Mockito.when(commitRepository.findFirstByRepository_NameOrderByDateDesc(captor.capture()))
                .thenReturn(Optional.of(commit));

        Optional<Commit> result = commitService.findLastCommitByRepositoryName(repoName);
        var captorValue = captor.getValue();

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(commit);
        assertThat(captorValue).isEqualTo(repoName);
    }

    @Test
    void whenFindLastCommitByRepositoryNameWithNoCommitThenReturnEmptyOptional() {
        Mockito.when(commitRepository.findFirstByRepository_NameOrderByDateDesc(Mockito.any()))
                .thenReturn(Optional.empty());

        Optional<Commit> result = commitService.findLastCommitByRepositoryName("repo");

        assertThat(result).isEmpty();
    }
}