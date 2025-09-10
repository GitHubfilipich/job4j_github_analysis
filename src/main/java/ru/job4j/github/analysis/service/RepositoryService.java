package ru.job4j.github.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.RepoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RepositoryService {
    @Autowired
    private RepoRepository repoRepository;

    @Async
    public void create(Repository repository) {
        repoRepository.save(repository);
    }

    public List<Repository> findAll() {
        return repoRepository.findAll();
    }

    public Optional<Repository> findByName(String name) {
        return repoRepository.findByName(name);
    }

    @Async
    public void save(Repository repository) {
        repoRepository.save(repository);
    }
}
