package ru.job4j.github.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.repository.CommitRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CommitService {
    @Autowired
    private CommitRepository commitRepository;

    public void create(Commit commit) {
        commitRepository.save(commit);
    }

    public List<RepositoryCommits> findCommitsByRepositoryName(String name) {
        return commitRepository.findByRepository_Name(name).stream()
                .map(this::getDTOFromCommit)
                .toList();
    }

    private RepositoryCommits getDTOFromCommit(Commit commit) {
        return new RepositoryCommits(
            new RepositoryCommits.CommitDetail(
                new RepositoryCommits.CommitAuthor(
                    commit.getAuthor(),
                    commit.getDate().toString()
                ),
                commit.getMessage()
            )
        );
    }

    public Optional<Commit> findLastCommitByRepositoryName(String name) {
        return commitRepository.findFirstByRepository_NameOrderByDateDesc(name);
    }
}
