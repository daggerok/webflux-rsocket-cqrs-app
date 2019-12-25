// package com.example.users;
//
// import lombok.*;
// import lombok.experimental.Accessors;
// import lombok.extern.log4j.Log4j2;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.r2dbc.repository.R2dbcRepository;
// import org.springframework.data.relational.core.mapping.Table;
// import org.springframework.data.web.PageableDefault;
// import org.springframework.http.MediaType;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.ResponseBody;
// import reactor.core.publisher.EmitterProcessor;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.FluxProcessor;
// import reactor.core.publisher.Mono;
// import reactor.core.scheduler.Scheduler;
// import reactor.core.scheduler.Schedulers;
//
// import java.awt.print.Pageable;
// import java.util.Optional;
// import java.util.UUID;
// import java.util.function.Consumer;
//
// /**
//  * Using db functionality to generate new entity UUID PK.
//  */
// @SpringBootApplication
// public class UsersRestApplication {
//
//     public static void main(String[] args) {
//         SpringApplication.run(UsersRestApplication.class, args);
//     }
// }
//
// /* User */
//
// @Data
// @Table("users")
// @NoArgsConstructor
// @Accessors(chain = true)
// @AllArgsConstructor(staticName = "of")
// @RequiredArgsConstructor(staticName = "of")
// class User {
//
//     @Id
//     private UUID id;
//
//     @NonNull
//     private String name, username;
// }
//
// interface UserRepository extends R2dbcRepository<User, UUID> {
// }
//
// /* User Command */
//
// @Log4j2
// @Controller
// @RequiredArgsConstructor
// class UserCommandResource {
//
//     private final UserRepository userRepository;
//     private final Consumer<User> saveUserPublisher;
//
//     @MessageMapping("save-user")
//     Mono<User> save(@RequestBody User user) {
//         return userRepository.save(user)/* <- append eventlog */
//         /* notify event -> */.handle((u, sink) -> saveUserPublisher.accept(u));
//     }
//
//     @PostMapping
//     @ResponseBody
//     Mono<Void> saveUser(@RequestBody User user) {
//         return userRepository.save(user)/* <- append eventlog */
//         /* notify event -> */.handle((u, sink) -> saveUserPublisher.accept(u));
//     }
// }
//
// /* User Query */
//
// @Configuration
// class UserStreamConfig {
//
//     @Bean
//     Scheduler userScheduler() {
//         return Schedulers.single();
//     }
//
//     @Bean
//     FluxProcessor<User, User> userProcessor() {
//         return EmitterProcessor.create();
//     }
//
//     @Bean
//     Consumer<User> userPublisher(FluxProcessor<User, User> userProcessor) {
//         return userProcessor::onNext;
//     }
//
//     @Bean
//     Flux<User> userSubscription(Scheduler userScheduler,
//                                 FluxProcessor<User, User> userProcessor) {
//
//         return userProcessor.publishOn(userScheduler)
//                             .subscribeOn(userScheduler)
//                             .share();
//     }
// }
//
// @Data
// @NoArgsConstructor
// class FindUsersRequest {
//     private Long size;
// }
//
// @Log4j2
// @Controller
// @RequiredArgsConstructor
// class UserQueryResource {
//
//     private final Flux<User> userSubscription;
//     private final UserRepository userRepository;
//
//     @ResponseBody
//     @GetMapping(produces = {
//             MediaType.APPLICATION_STREAM_JSON_VALUE,
//             MediaType.TEXT_EVENT_STREAM_VALUE,
//             MediaType.APPLICATION_JSON_VALUE,
//     })
//     Flux<User> findUsersOverRest(@PageableDefault(size = 25) Pageable pageable) {
//         return userRepository.findAll();
//     }
//
//     @ResponseBody
//     @GetMapping(path = "/stream", produces = {
//             MediaType.APPLICATION_STREAM_JSON_VALUE,
//             MediaType.TEXT_EVENT_STREAM_VALUE,
//             MediaType.APPLICATION_JSON_VALUE,
//     })
//     Flux<User> streamUsersOverRest() {
//         return userSubscription;
//     }
//
//     @MessageMapping("query-users")
//     Flux<User> findUsersOverRSocket(FindUsersRequest request) {
//         var maybeSize = Optional.ofNullable(request)
//                                 .map(FindUsersRequest::getSize);
//         return Mono.justOrEmpty(maybeSize)
//                    .flatMapMany(nr -> userRepository.findAll()
//                                                     .take(nr))
//                    .switchIfEmpty(userRepository.findAll());
//     }
//
//     @MessageMapping("stream-users")
//     Flux<User> streamUsersOverRSocket() {
//         return userSubscription;
//     }
// }
