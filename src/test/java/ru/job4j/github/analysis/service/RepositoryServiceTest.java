package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryServiceTest {
    private RepoRepository repoRepository;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        repoRepository = Mockito.mock(RepoRepository.class);
        repositoryService = new RepositoryService(repoRepository);
    }

    @Test
    void whenCreateThenRepositorySaveCalled() {
        Repository repo = new Repository(1L, "repo1", "url1");

        repositoryService.create(repo);

        ArgumentCaptor<Repository> captor = ArgumentCaptor.forClass(Repository.class);
        Mockito.verify(repoRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(repo);
    }

    @Test
    void whenFindAllThenReturnList() {
        List<Repository> repos = List.of(
                new Repository(1L, "repo1", "url1"),
                new Repository(2L, "repo2", "url2")
        );
        Mockito.when(repoRepository.findAll()).thenReturn(repos);

        List<Repository> result = repositoryService.findAll();

        assertThat(result).isEqualTo(repos);
    }

    @Test
    void whenFindByNameThenReturnOptional() {
        Repository repo = new Repository(1L, "repo1", "url1");
        Mockito.when(repoRepository.findByName("repo1")).thenReturn(Optional.of(repo));

        Optional<Repository> result = repositoryService.findByName("repo1");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(repo);
    }

    @Test
    void whenFindByNameNotFoundThenReturnEmptyOptional() {
        Mockito.when(repoRepository.findByName("repoX")).thenReturn(Optional.empty());
        Optional<Repository> result = repositoryService.findByName("repoX");
        assertThat(result).isEmpty();
    }

    @Test
    void whenSaveThenRepositorySaveCalled() {
        Repository repo = new Repository(2L, "repo2", "url2");

        repositoryService.save(repo);

        ArgumentCaptor<Repository> captor = ArgumentCaptor.forClass(Repository.class);
        Mockito.verify(repoRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(repo);
    }
}