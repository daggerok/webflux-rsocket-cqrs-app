// package com.example.users;
//
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import lombok.*;
// import lombok.experimental.Accessors;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.annotation.Transient;
// import org.springframework.data.r2dbc.repository.R2dbcRepository;
// import org.springframework.data.relational.core.mapping.Table;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// import java.util.Optional;
// import java.util.UUID;
//
// /**
//  * Using {@link org.springframework.data.domain.Persistable} to
//  * identify whenever entity is new. Generating UUID there as well.
//  */
// @SpringBootApplication
// public class Users0Application {
//
//     public static void main(String[] args) {
//         SpringApplication.run(Users0Application.class, args);
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
// class User implements org.springframework.data.domain.Persistable<UUID> {
//
//     @Id
//     private UUID id;
//
//     @NonNull
//     private String name, username;
//
//     @Override
//     @Transient
//     @JsonIgnore
//     public boolean isNew() { // https://github.com/spring-projects/spring-data-r2dbc/issues/218#issuecomment-545767727
//         var maybeId = Optional.ofNullable(this.id);
//         id = maybeId.orElseGet(UUID::randomUUID);
//         return maybeId.map(UUID::toString)
//                       .filter("00000000-"::startsWith)
//                       .isEmpty();
//     }
// }
//
// interface UserRepository extends R2dbcRepository<User, UUID> { }
//
// /* Save user command */
//
// @RestController
// @RequiredArgsConstructor
// class UserResource {
//
//     private final UserRepository userRepository;
//
//     @GetMapping(produces = {
//             MediaType.APPLICATION_STREAM_JSON_VALUE,
//             MediaType.TEXT_EVENT_STREAM_VALUE,
//             MediaType.APPLICATION_JSON_VALUE,
//     })
//     Flux<User> findAll() {
//         return userRepository.findAll();
//     }
//
//     @PostMapping
//     Mono<User> save(@RequestBody User user) {
//         return userRepository.save(user);
//     }
// }
