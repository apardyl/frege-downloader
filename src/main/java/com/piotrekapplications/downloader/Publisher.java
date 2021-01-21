package com.piotrekapplications.downloader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class Publisher {
    private final String EXTRACT_QUEUE = "extract";
    private final Logger logger = LoggerFactory.getLogger(Publisher.class);

    @Value("${downloader.publish.delay}")
    private int delay;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void publishMessage(String repoId, ArrayList<Integer> languages) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonToSend = null;
        try {
            Map<String,Object> map;
            if (languages != null && languages.size() != 0)  {
                map = Map.of("repo_id", repoId, "languages", languages);
            } else {
                map = Map.of("repo_id", repoId);
            }
            jsonToSend = mapper.writeValueAsString(map);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");
            Message message = new Message(jsonToSend.getBytes(), messageProperties);
            tryToSendMessageInLoop(message);
        } catch (JsonProcessingException e) {
            logger.error("Cant parse received message to json");
        }
    }

    private void tryToSendMessageInLoop(Message message) {
        while (true) {
            Boolean result = rabbitTemplate.invoke(t -> {
                t.send(EXTRACT_QUEUE, message);
                return t.waitForConfirms(10_000);
            }, (tag, multiple) -> {
                logger.info("Ack received");
            }, (tag, multiple) -> {
                logger.info("Nack received");
            });
            if (result != null && result) {
                logger.info("Ack received.Message published successfully");
                break;
            } else {
                logger.info("Nack received. I will try send message again.");
                try {
                    Thread.sleep(delay * 1000L);
                } catch (InterruptedException e) {
                    logger.error("Thread was interrupted!");
                }
            }
        }
    }
}
