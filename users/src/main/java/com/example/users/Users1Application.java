// package com.example.users;
//
// import lombok.*;
// import lombok.experimental.Accessors;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.r2dbc.repository.R2dbcRepository;
// import org.springframework.data.relational.core.mapping.Table;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// import java.util.UUID;
//
// /**
//  * Using db functionality to generate new entity UUID PK.
//  */
// @SpringBootApplication
// public class Users1Application {
//
//     public static void main(String[] args) {
//         SpringApplication.run(Users1Application.class, args);
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
//     @GetMapping
//     Flux<User> on() {
//         return userRepository.findAll();
//     }
//
//     @PostMapping
//     Mono<User> save(@RequestBody User user) {
//         return userRepository.save(user);
//     }
// }
