package ru.job4j.github.analysis.service;

import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.repository.CommitRepository;

import java.util.List;

@Service
public class CommitService {
    private CommitRepository commitRepository;

    public void create(Commit commit) {
        commitRepository.save(commit);
    }

    public List<RepositoryCommits> findCommitsByRepositoryName(String name) {
        return commitRepository.findByRepositoryName(name).stream()
                .map(c -> new RepositoryCommits(c.getMessage(), c.getAuthor(), c.getDate()))
                .toList();
    }
}
