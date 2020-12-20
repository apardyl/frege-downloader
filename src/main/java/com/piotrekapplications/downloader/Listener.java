package com.piotrekapplications.downloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@Component
public class Listener {

    @Autowired
    Publisher publisher;

    @RabbitListener(queues = "download")
    public void listen(Message event) throws IOException, GitAPIException {
        String s = new String(event.getBody(), StandardCharsets.UTF_8);
        System.out.println(s);
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(s, Map.class);
        Git clone = downloadRepository((String) map.get("repo_id"), (String) map.get("git_url"));
        Ref head  = clone.getRepository().findRef("HEAD");
        System.out.println("Ref of HEAD: " + head + ": " + head.getName() + " - " + head.getObjectId().getName());
        publisher.publishMessage((String) map.get("repo_id"));

    }

    private Git downloadRepository(String repo_id, String git_url) throws GitAPIException {
        String repo = String.format("/home/piotr/Documents/repositories/%s", repo_id);
        return Git.cloneRepository()
                .setURI(git_url)
                .setDirectory(new File(repo))
                .call();


    }
}
