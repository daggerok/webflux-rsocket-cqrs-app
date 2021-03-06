# webflux-rsocket-cqrs-app [![Build Status](https://travis-ci.org/daggerok/webflux-rsocket-cqrs-app.svg?branch=master)](https://travis-ci.org/daggerok/webflux-rsocket-cqrs-app)
Play with Spring Webflux, RSocket API, R2DBC and reactive streams...

_Full non-blocking end-to-end reactive stack by using RSocket, WebFlux, R2DBC and reactive streams!_

```
                  users-command
                    +-------+     TCP
         COMMAND    | WRITE |   RSocket
         HTTP ,>-~->~  SIDE ~>-~->~.                             _______
             /      | :8081 |       \  shared +-------+         '       `
   _O_ -~->~'       +-------+        `-~->-~->~ :7000 |         |  D B  |
    |                                | single | users <~-R2DBC-~> EVENT |
   / \ -<-~-.       +-------+        ,-<-~-<-~- :7000 |         |  LOG  |
             \      |  READ |       / connect.+-------+         `_______,
         HTTP `<-~-<~  SIDE <-~-~<-'          
         QUERY      | :8082 |   RSocket
         STREAM     +-------+     TCP
                   users-query
```

**required jdk 11**

```bash
# jdk11
jenv local 11.0
# build
./mvnw
# run
java -jar         ./users/target/*.jar &
java -jar   ./users-query/target/*.jar &
java -jar ./users-command/target/*.jar &
# queries
http get      :8082/find-users
curl localhost:8082/stream-users
# commands
http post :8081 name=ololo   username=ololo
http post :8081 name=trololo username=trololo
# teardown
killall -9 java
```

**parallel subscription filtering**

```bash
# subscribe with filters:
curl "localhost:8082/stream-users/?query=max&?query=fax" &
http --stream ":8082/stream-users?query=billy&?query=bob" Accept:"application/stream+json" &
# this won't be delivered to subscribers:
http :8081 name="Maksim Ko" username=maksimko >/dev/null
http :8081 name="Ololo Trololo" username=ololo.trololo >/dev/null
# this will be seen:delivered only to second subscriber:
http :8081 name="Billy Bob Thornton" username=billy.bob >/dev/null
# this will be seen:delivered to both subscriber:
http :8081 name="Billy" username=Max >/dev/null
```

## resources

* [Reactor mapOrEmpty workaround](https://github.com/reactor/reactor-core/issues/1952)
* [WebFlux FluxProcessors, FluxSink, WebFlux Emitter, broadcasters](https://github.com/daggerok/webflux-reactor-broadcaster/blob/master/src/main/java/com/example/broadcaster/MyBroadcaster.java)
* [H2 RANDOM_UUID()](https://stackoverflow.com/questions/41112332/generate-uuid-values-by-default-for-each-row-on-column-of-uuid-type-in-h2-databa) 
* [Some R2DBC helpful issue with solutions](https://github.com/spring-projects/spring-data-r2dbc/issues/218)
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/maven-plugin/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Data R2DBC [Experimental]](https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [R2DBC example](https://github.com/spring-projects-experimental/spring-boot-r2dbc/tree/master/spring-boot-example-h2)
* [R2DBC Homepage](https://r2dbc.io)
