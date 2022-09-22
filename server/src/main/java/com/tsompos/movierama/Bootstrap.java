package com.tsompos.movierama;

import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.User;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import com.tsompos.movierama.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Bootstrap {

    private final MovieRecommendationRepository movieRecommendationRepository;
    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        User publisher = userRepository.save(User.builder().userName("john").build()).block();
        var firstMovie = MovieRecommendation.builder()
                                            .title("A Greek")
                                            .description("aDescription")
                                            .publisherId(publisher.getId())
                                            .likes(2L)
                                            .build();

        var secondMovie = MovieRecommendation.builder()
                                             .title("Big")
                                             .description("aDescription")
                                             .publisherId(publisher.getId())
                                             .build();

        var thirdMovie = MovieRecommendation.builder()
                                            .title("Fat")
                                            .description("aDescription")
                                            .publisherId(publisher.getId())
                                            .build();

        var fourthMovie = MovieRecommendation.builder()
                                             .title("Family")
                                             .description("aDescription")
                                             .publisherId(publisher.getId())
                                             .build();

        movieRecommendationRepository.saveAll(List.of(firstMovie, secondMovie, thirdMovie, fourthMovie)).blockLast();
    }
}
