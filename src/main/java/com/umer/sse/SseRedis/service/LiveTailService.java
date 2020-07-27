package com.umer.sse.SseRedis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LiveTailService {

    @Value("${sseemitter.timeoutInMinutes}")
    private long timeout;

    @Autowired
    RedisMessageListenerContainer redisContainer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LiveTailService.class);

    /**
     * Create and returns a new instance of {@link SseEmitter} bound to the Redis channel <code>channelName</code> which
     * emits any messages received on the channel.
     *
     * @param channelName Redis channel name
     * @return new instance of {@link SseEmitter}
     */
    public SseEmitter newSseEmitterForRedisChannel(final String channelName) {
        final SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(timeout));

        MessageListener messageListener = (message, pattern) -> {
            final AtomicInteger messageIdCounter = new AtomicInteger();
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

            logger.debug("Received {} on {}", message, Thread.currentThread().getName() );
        };

        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(messageListener);

        redisContainer.addMessageListener(listenerAdapter, new PatternTopic(getChannelNameWithPrefix(channelName)));
        logger.info("Added emitter {} from listenerAdapter {}", emitter, listenerAdapter);

        emitter.onCompletion(() -> {
            logger.info("Removed emitter {} from listenerAdapter {}", emitter, listenerAdapter);
            redisContainer.removeMessageListener(listenerAdapter);
        });

        return emitter;
    }

    public void publicMessageToRedisChannel(String channelName, String message) {
        redisTemplate.convertAndSend(getChannelNameWithPrefix(channelName), message);
    }

    // Add a prefix to the channel name (Not aware of a way of finding all channels using wildcard in Redis)
    private String getChannelNameWithPrefix(String channelName) {
        return "pubsub:" + channelName;
    }
}
