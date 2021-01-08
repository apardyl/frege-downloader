package com.piotrekapplications.downloader.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryRepo extends JpaRepository<Repositories, String> {
}
