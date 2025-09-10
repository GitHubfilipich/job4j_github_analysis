package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.github.analysis.model.Commit;

import java.util.List;
import java.util.Optional;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByRepository_Name(String name);

    Optional<Commit> findFirstByRepository_NameOrderByDateDesc(String repositoryName);
}
