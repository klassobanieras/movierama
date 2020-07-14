package com.tsompos.movierama.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableJpaAuditing
@EnableSpringDataWebSupport
public class ApplicationConfiguration {

    public static final String MOVIES_URL = "/movies";
}
