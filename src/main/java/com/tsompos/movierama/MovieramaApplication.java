package com.tsompos.movierama;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableSpringDataWebSupport
public class MovieramaApplication {

    public static void main(String[] args) {
        run(MovieramaApplication.class, args);
    }

}
