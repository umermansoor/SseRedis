package com.umer.sse.SseRedis.controllers;

import com.umer.sse.SseRedis.service.LiveTailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(path = "/api/v1/livetail")
public class LiveTailController {

    @Autowired
    private LiveTailService liveTailService;

    private static final Logger logger = LoggerFactory.getLogger(LiveTailController.class);

    @GetMapping(path = "/sse/{channelName}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public SseEmitter liveTailSubscriber(@PathVariable String channelName) {
        return liveTailService.newSseEmitterForRedisChannel(channelName);
    }

    @RequestMapping(value = "/sse/{channelName}/message/{message}", method = POST)
    @ResponseBody
    public String liveTailProduce(@PathVariable String channelName, @PathVariable String message) {
        liveTailService.publicMessageToRedisChannel(channelName, message);
        return "OK";
    }
}
