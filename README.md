# Server Sent Events with Redis Pub Sub

Server Sent Events are the data (events) sent from server to the client over HTTP connection. It is a one way communication channel (server to client) compared to WebSocket (full duplex). 

This example project shows how to create a hypothetical "Live Tail" feature. Events are published to a Redis channel, which in turn are consumed and sent to connected clients in using Server Sent Events.

See `application.properties` for configurable values.

## Pre-requisite
Redis is installed locally and is running. Use `brew install redis` if you don't have Redis installed (OSX.)

## How to use
1. Start Application: `mvn spring-boot:run`
2. Visit `http://localhost:8080/api/v1/livetail/sse/1`. Here, 1 is the name the Redis channel where the emitter is subscribed.
3. To send a message to the channel subscribed to in t he last step: ` curl -X POST http://localhost:8080/api/v1/livetail/sse/1/message/hellofromcurl`
4. (Optional) Send a message to the channel directly using redis e.g. `redis-cli`: `PUBLISH 1 hello`
5. The message will show up on the webpage from step 2.