package com.piotrekapplications.downloader;

import com.piotrekapplications.downloader.database.Repositories;
import com.piotrekapplications.downloader.database.RepositoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;

@SpringBootApplication
public class DownloaderApplication implements CommandLineRunner {
    @Autowired
    private RepositoryRepo database;

    public static void main(String[] args) {
        SpringApplication.run(DownloaderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Repositories repositories = new Repositories("some_id","https://github.com/Software-Engineering-Jagiellonian/frege-indexer-lib.git",
                "repo_url",new Timestamp(System.currentTimeMillis()), null, null);
        database.save(repositories);
    }
}
