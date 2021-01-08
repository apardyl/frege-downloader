package com.piotrekapplications.downloader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Publisher {
    private final String EXTRACT_QUEUE = "extract";

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void publishMessage(String repoId) throws GitAPIException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Map.of("repo_id", repoId));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);
        Boolean result = rabbitTemplate.invoke(t -> {
            t.send(EXTRACT_QUEUE, message);
            t.waitForConfirmsOrDie(10_000);
            return true;
        }, (tag, multiple) -> {
            System.out.println("Ack: " + tag + ":" + multiple);
        }, (tag, multiple) -> {
            System.out.println("Nack: " + tag + ":" + multiple);
        });
    }
}
