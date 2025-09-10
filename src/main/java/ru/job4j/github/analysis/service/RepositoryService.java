package ru.job4j.github.analysis.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.util.List;

@Service
public class RepositoryService {
    private RepoRepository repoRepository;

    @Async
    public void create(Repository repository) {
        repoRepository.save(repository);
    }

    public List<Repository> findAll() {
        return repoRepository.findAll();
    }
}
