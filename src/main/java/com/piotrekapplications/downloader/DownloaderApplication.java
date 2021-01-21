package com.piotrekapplications.downloader;

import com.piotrekapplications.downloader.database.Repositories;
import com.piotrekapplications.downloader.database.RepositoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;

@SpringBootApplication
public class DownloaderApplication  {

    public static void main(String[] args) {
        SpringApplication.run(DownloaderApplication.class, args);
    }


}
