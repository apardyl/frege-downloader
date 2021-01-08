package com.piotrekapplications.downloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piotrekapplications.downloader.database.DatabaseService;
import com.rabbitmq.client.Channel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
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
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class Listener {

    @Autowired
    Publisher publisher;

    @Autowired
    DatabaseService databaseService;


    @RabbitListener(queues = "download", ackMode = "MANUAL")
    public void listen(Message event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, GitAPIException {
        String s = new String(event.getBody(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(s, Map.class);
        String repoId = (String) map.get("repo_id");
        String gitUrl = (String) map.get("git_url");
        Git clone = downloadRepository(repoId, gitUrl);
        Ref head = clone.getRepository().findRef("HEAD");
        String commitHash = head.getObjectId().getName();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        databaseService.updateCommitHashAndTimestamp(repoId,commitHash,timestamp);
        publisher.publishMessage((String) map.get("repo_id"));
        channel.basicNack(tag, false, false);

    }

    private Git downloadRepository(String repo_id, String git_url) throws GitAPIException {
        String repo = String.format("/home/piotr/Documents/repositories/%s", repo_id);
        return Git.cloneRepository()
                .setURI(git_url)
                .setDirectory(new File(repo))
                .call();
    }
    @RabbitListener(queues = "extract",ackMode = "MANUAL")
    public void receiveMessage(Message event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, GitAPIException, InterruptedException {
        String s = new String(event.getBody(), StandardCharsets.UTF_8);
        System.out.println("recieved message:"+ s);
        channel.basicNack(tag, false, false);
    }

}
