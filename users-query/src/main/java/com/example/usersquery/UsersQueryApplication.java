package com.example.usersquery;

import com.example.users.shared.FindUsersRequest;
import com.example.users.shared.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class UserQueryResource {

    private final Mono<RSocketRequester> rSocket; // comes from shared...

    @GetMapping(path = "/find-users", produces = {
            MediaType.APPLICATION_STREAM_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
    })
    Flux<User> findAllUsers() {
        return rSocket.flatMapMany(rr -> rr.route("find-users")
                                           .data(Mono.justOrEmpty(new FindUsersRequest()))
                                           .retrieveFlux(User.class));
    }

    @GetMapping(path = "/stream-users", produces = {
            MediaType.APPLICATION_STREAM_JSON_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE,
    })
    Flux<User> streamUsers(@RequestParam(name = "query", required = true) List<String> query) {
        var qs = query.stream().map(String::toLowerCase).collect(Collectors.toList());
        var stream = rSocket.flatMapMany(rr -> rr.route("stream-users")
                                                 .retrieveFlux(User.class));
        return qs.isEmpty() ? stream // @formatter:off
                : stream.filter(user -> qs.stream().anyMatch(user.getUsername().toLowerCase()::contains) ||
                                        qs.stream().anyMatch(user.getName().toLowerCase()::contains));
        // @formatter:on
    }

    @GetMapping
    Mono<Map<String, String>> fallback(ServerWebExchange exchange) {
        var uri = exchange.getRequest().getURI();
        Function<String, String> url = path -> String.format("%s://%s/%s", uri.getScheme(), uri.getAuthority(), path);
        return Mono.just(Map.of("find users GET", url.apply("find-users"),
                                "stream users GET", url.apply("stream-users")));
    }
}

@SpringBootApplication
public class UsersQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersQueryApplication.class, args);
    }
}
