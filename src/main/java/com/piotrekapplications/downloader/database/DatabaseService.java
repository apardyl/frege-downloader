package com.piotrekapplications.downloader.database;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
@Transactional
@Service
public class DatabaseService {

    private final RepositoryRepo repositoryRepo;

    public DatabaseService(RepositoryRepo repositoryRepo) {
        this.repositoryRepo = repositoryRepo;
    }

    public void updateCommitHashAndTimestamp(String repoId, String commitHash, Timestamp downloadTime) {
        Repositories repositories = repositoryRepo.getOne(repoId);
        repositories.setCommitHash(commitHash);
        repositories.setDownloadTime(downloadTime);
        repositoryRepo.save(repositories);
    }
}
