package com.umer.sse.SseRedis.controllers;

import com.umer.sse.SseRedis.listeners.RedisReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(path = "/api/v1/livetail")
public class LiveTailController {

    @Value("${sseemitter.timeoutInMinutes}")
    private long timeout;

    private static final Logger logger = LoggerFactory.getLogger(LiveTailController.class);

    @Autowired
    RedisMessageListenerContainer redisContainer;

    @GetMapping(path = "/sse/{accountId}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public SseEmitter liveTailEvents(@PathVariable int accountId) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(timeout));

        // TODO Move to service
        RedisReceiver redisReceiver = new RedisReceiver(emitter);
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(redisReceiver);
        redisContainer.addMessageListener(listenerAdapter, new PatternTopic(String.valueOf(accountId)));
        logger.info("Added emitter {} from listenerAdapter {}", emitter, listenerAdapter);

        emitter.onCompletion( () -> {
            logger.info("Removed emitter {} from listenerAdapter {}", emitter, listenerAdapter);
            redisContainer.removeMessageListener(listenerAdapter);
        });

        return emitter;
    }
}
