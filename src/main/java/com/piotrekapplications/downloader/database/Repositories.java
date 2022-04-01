package com.piotrekapplications.downloader.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name="repositories")
public class Repositories {
    @Id
    @Column(name="repo_id")
    private String repoId;
    @Column(name="git_url")
    private String gitUrl;
    @Column(name="repo_url")
    private String repoUrl;
    @Column(name="crawl_time")
    private Timestamp crawlTime;
    @Column(name="download_time")
    private Timestamp downloadTime;
    @Column(name="commit_hash")
    private String commitHash;

    public Repositories(String repoId, String gitUrl, String repoUrl, Timestamp crawlTime, Timestamp downloadTime, String commitHash) {
        this.repoId = repoId;
        this.gitUrl = gitUrl;
        this.repoUrl = repoUrl;
        this.crawlTime = crawlTime;
        this.downloadTime = downloadTime;
        this.commitHash = commitHash;
    }
    public Repositories() {

    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public Timestamp getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(Timestamp crawlTime) {
        this.crawlTime = crawlTime;
    }

    public Timestamp getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Timestamp downloadTime) {
        this.downloadTime = downloadTime;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }
}
