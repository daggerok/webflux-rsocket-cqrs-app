package com.example.users.shared;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UsersProps.class)
@ComponentScan(basePackageClasses = UsersSharedAutoConfiguration.class)
public class UsersSharedAutoConfiguration { }
