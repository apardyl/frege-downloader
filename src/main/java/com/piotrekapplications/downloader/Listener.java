package com.piotrekapplications.downloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrekapplications.downloader.database.DatabaseService;
import com.rabbitmq.client.Channel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class Listener {

    @Autowired
    Publisher publisher;

    @Autowired
    DatabaseService databaseService;

    private final Logger logger = LoggerFactory.getLogger(Listener.class);


    @RabbitListener(queues = "download", ackMode = "MANUAL")
    public void listen(Message event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, GitAPIException {
        String messageReceived = new String(event.getBody(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(messageReceived, Map.class);
        String repoId = (String) map.get("repo_id");
        String gitUrl = (String) map.get("git_url");
        ArrayList<Integer> languages = (ArrayList<Integer>) map.get("languages");
        Git downloadedRepository = downloadRepository(repoId, gitUrl);
        if(downloadedRepository != null) {
            retrieveCommitHashAndUpdateDatabase(downloadedRepository, repoId);
            publisher.publishMessage(repoId, languages);
        }
        channel.basicAck(tag, false);
    }

    private void retrieveCommitHashAndUpdateDatabase(Git downloadedRepository, String repoId) {

        try {
            Ref head = downloadedRepository.getRepository()
                    .findRef("HEAD");
            String commitHash = head.getObjectId().getName();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            databaseService.updateCommitHashAndTimestamp(repoId, commitHash, timestamp);
        } catch (IOException e) {
            logger.error("Cant retrieve commitHash from Repository", e);
        }

    }

    private Git downloadRepository(String repo_id, String git_url) {
        String repoDirectory = String.format("/repositories/%s", repo_id);
        Git downloadedRepository = null;
        try {
            downloadedRepository = Git.cloneRepository()
                    .setURI(git_url)
                    .setDirectory(new File(repoDirectory))
                    .call();
        } catch (GitAPIException e) {
            logger.error("Can't download repository", e);
        }
        return downloadedRepository;
    }

}
