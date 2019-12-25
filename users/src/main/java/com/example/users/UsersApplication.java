package com.example.users;

import com.example.users.shared.FindUsersRequest;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Using db functionality to generate new entity UUID PK.
 */
@SpringBootApplication
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }
}

/* User */

@Data
@Table("users")
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor(staticName = "of")
@RequiredArgsConstructor(staticName = "of")
class User {

    @Id
    private UUID id;

    @NonNull
    private String name, username;
}

interface UserRepository extends R2dbcRepository<User, UUID> { }

/* User Command */

@Log4j2
@Controller
@RequiredArgsConstructor
class UserCommandResource {

    private final UserRepository userRepository;
    private final Consumer<User> saveUserPublisher;

    @MessageMapping("save-user")
    Mono<User> save(@RequestBody User user) {
        return userRepository.save(user) // <-- append to eventlog and fire saved event
                             //.handle((u, sink) -> saveUserPublisher.accept(u));
                             .doOnSuccess(saveUserPublisher);
    }
}

/* User Query */

@Log4j2
@Configuration
class UserStreamConfig {

    @Bean
    Scheduler userScheduler() {
        return Schedulers.single();
    }

    @Bean
    FluxProcessor<User, User> userProcessor() {
        return EmitterProcessor.create();
    }

    @Bean
    Consumer<User> userPublisher(FluxProcessor<User, User> userProcessor) {
        return userProcessor::onNext;
    }

    @Bean
    Flux<User> userSubscription(Scheduler userScheduler,
                                FluxProcessor<User, User> userProcessor) {

        return userProcessor.publishOn(userScheduler)
                            .subscribeOn(userScheduler)
                            .onBackpressureBuffer()
                            .share();
    }
}

@Log4j2
@Component
@RequiredArgsConstructor
class SharedUserSubscriberLogger {

    private final Flux<User> userSubscription;

    @PostConstruct
    public void collect() throws Exception {
        log.info("setting up logging subscription...");
        userSubscription.subscribe(log::info);
    }
}

@Log4j2
@Controller
@RequiredArgsConstructor
class UserQueryResource {

    private final Flux<User> userSubscription;
    private final UserRepository userRepository;

    @MessageMapping("find-users")
    Flux<User> findUsers(@RequestBody FindUsersRequest request) {
        var maybeSize = Optional.ofNullable(request)
                                .map(FindUsersRequest::getSize)
                                .filter(l -> l > 0);
        return Mono.justOrEmpty(maybeSize)
                   .flatMapMany(amount -> userRepository.findAll()
                                                        .take(amount))
                   .switchIfEmpty(userRepository.findAll());
    }

    @MessageMapping("stream-users")
    Flux<User> streamUsers() {
        return userSubscription;
    }

    // @MessageExceptionHandler(RuntimeException.class)
    // Flux<User> fallback(RuntimeException e) { // Use own result objects instead...
    //     return Flux.just(new User().setId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    //                                .setName(e.getLocalizedMessage())
    //                                .setUsername(e.getClass().getName() + ".fallback.error"));
    // }
}
