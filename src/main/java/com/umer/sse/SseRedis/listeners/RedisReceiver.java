package com.umer.sse.SseRedis.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;


public class RedisReceiver implements MessageListener {
    private SseEmitter emitter;
    private AtomicInteger messageIdCounter = new AtomicInteger();

    private static final Logger logger = LoggerFactory.getLogger(RedisReceiver.class);

    public RedisReceiver(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data(LocalTime.now().toString() + " [LiveTail Event] - " + message)
                    .id(String.valueOf(messageIdCounter.getAndIncrement()))
                    .name("livetail-event");
            emitter.send(event);
        } catch (IOException ioe) {
            logger.error("Emitter exception {}", ioe.getMessage());
            emitter.completeWithError(ioe);
        }

        logger.info("Received {} on {}", message, Thread.currentThread().getName() );
    }

    public void receiveMessage(String message) {
//        try {
//            SseEmitter.SseEventBuilder event = SseEmitter.event()
//                    .data(LocalTime.now().toString() + " [LiveTail Event] - " + message)
//                    .id(String.valueOf(messageIdCounter.getAndIncrement()))
//                    .name("livetail-event");
//            emitter.send(event);
//        } catch (IOException ioe) {
//            logger.error("Emitter exception {}", ioe.getMessage());
//            emitter.completeWithError(ioe);
//        }


        logger.info("Received <" + message + ">");
    }


}
