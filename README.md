# Server Sent Events with Redis Pub Sub

See `application.properties` for configurable values.

## Pre-requisite
Redis is installed locally and is running. Use `brew install redis` if you don't have Redis installed (OSX.)

## How to use
1. Start Application: `mvn spring-boot:run`
2. Visit `http://localhost:8080/api/v1/livetail/sse/1`. Here, 1 is the name the Redis channel where the emitter is subscribed
3. Send a message to the channel using redis e.g. `redis-cli`: `PUBLISH 1 hello`
4. The message will show up on the webpage