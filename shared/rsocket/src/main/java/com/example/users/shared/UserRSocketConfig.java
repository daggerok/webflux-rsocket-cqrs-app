package com.example.users.shared;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.rsocket.netty.NettyRSocketServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnClass({
        UsersProps.class,
        NettyRSocketServerFactory.class,
})
public class UserRSocketConfig {

    @Bean
    @ConditionalOnMissingBean
    public Mono<RSocketRequester> rSocket(UsersProps props,
                                          RSocketRequester.Builder builder) {

        return builder.connectTcp(props.getHost(), props.getPort());
    }
}
