package com.tsompos.movierama;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Bootstrap {

    private final MovieRecommendationRepository movieRecommendationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        MovieRecommendation firstMovieRecommendation = MovieRecommendation.builder()
                .title("A Greek")
                .description("aDescription")
                .publishedBy("john")
                .countOfLikes(5L)
                .countOfHates(1L)
                .build();

        MovieRecommendation secondMovie = MovieRecommendation.builder()
                .title("Big")
                .description("aDescription")
                .publishedBy("john")
                .countOfLikes(5L)
                .countOfHates(1L)
                .build();

        MovieRecommendation thirdMovie = MovieRecommendation.builder()
                .title("Fat")
                .description("aDescription")
                .publishedBy("john")
                .countOfLikes(3L)
                .countOfHates(1L)
                .build();

        MovieRecommendation fourthMovie = MovieRecommendation.builder()
                .title("Family")
                .description("aDescription")
                .publishedBy("john")
                .countOfLikes(9L)
                .countOfHates(1L)
                .build();

        movieRecommendationRepository.saveAll(List.of(firstMovieRecommendation, secondMovie, thirdMovie, fourthMovie));
    }
}
