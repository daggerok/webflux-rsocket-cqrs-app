package com.example.userscommand;

import com.example.users.shared.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
class UserCommandResource {

    private final Mono<RSocketRequester> rSocket; // comes from shared...

    @PostMapping
    Mono<User> save(@RequestBody User user) {
        return rSocket.flatMap(rs -> rs.route("save-user")
                                       .data(Mono.justOrEmpty(user)
                                                 .filter(u -> null != u.getName() && !u.getName().isBlank())
                                                 .filter(u -> null != u.getUsername() && !u.getUsername().isBlank()))
                                       .retrieveMono(User.class));
    }
}

@SpringBootApplication
public class UsersCommandApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersCommandApplication.class, args);
    }
}
