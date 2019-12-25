package com.example.users.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "users")
public class UsersProps {
    private String host;
    private Integer port;
}
