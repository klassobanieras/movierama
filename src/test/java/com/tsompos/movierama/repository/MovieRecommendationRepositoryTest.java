package com.tsompos.movierama.repository;

import com.tsompos.movierama.dto.MovieProjection;
import com.tsompos.movierama.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MovieRecommendationRepositoryTest {

    @Autowired
    private MovieRecommendationRepository movieRecommendationRepository;

    @Test
    void findAll() {
        movieRecommendationRepository.save(MovieRecommendation.builder()
            .title("aTitle")
            .description("a description")
            .userReactions(Set.of(UserReaction.builder().reaction(Reaction.LIKE).userId("1234").build(),
                UserReaction.builder().reaction(Reaction.LIKE).userId("12345").build()))
            .countOfLikes(2)
            .build());

        Page<MovieProjection> movies = movieRecommendationRepository.findAllBy(Pageable.unpaged());
        assertEquals(movies.getContent().size(), 1);
        assertEquals(movies.getContent().get(0).getCountOfLikes(), 2);
    }

    @Test
    void findAllSortByLikes() {
        movieRecommendationRepository.save(MovieRecommendation.builder().title("aTitle").description("a description").build());

        movieRecommendationRepository.save(MovieRecommendation.builder()
            .title("bTitle")
            .description("a description")
            .userReactions(Set.of(UserReaction.builder().reaction(Reaction.LIKE).userId("1234").build(),
                UserReaction.builder().reaction(Reaction.LIKE).userId("12345").build()))
            .countOfLikes(2)
            .build());

        movieRecommendationRepository.save(MovieRecommendation.builder()
            .title("cTitle")
            .description("a description")
            .userReactions(Set.of(UserReaction.builder().reaction(Reaction.LIKE).userId("1234").build()))
            .countOfLikes(1)
            .build());

        Page<MovieProjection> movies =
            movieRecommendationRepository.findAllBy(PageRequest.of(0, 10, Sort.by("countOfLikes").descending()));

        assertEquals(3, movies.getContent().size());
        assertEquals(2, movies.getContent().get(0).getCountOfLikes());
        assertEquals(1, movies.getContent().get(1).getCountOfLikes());
        assertEquals(0, movies.getContent().get(2).getCountOfLikes());
    }

    @Test
    void findAllSortByHates() {
        movieRecommendationRepository.save(MovieRecommendation.builder().title("aTitle").description("a description").build());

        movieRecommendationRepository.save(MovieRecommendation.builder()
            .title("bTitle")
            .description("a description")
            .userReactions(Set.of(UserReaction.builder().reaction(Reaction.HATE).userId("1234").build(),
                UserReaction.builder().reaction(Reaction.HATE).userId("12345").build()))
            .countOfHates(2)
            .build());

        movieRecommendationRepository.save(MovieRecommendation.builder()
            .title("cTitle")
            .description("a description")
            .userReactions(Set.of(UserReaction.builder().reaction(Reaction.HATE).userId("1234").build()))
            .countOfHates(1)
            .build());

        Page<MovieProjection> movies =
            movieRecommendationRepository.findAllBy(PageRequest.of(0, 10, Sort.by("countOfHates").descending()));

        assertEquals(3, movies.getContent().size());
        assertEquals(2, movies.getContent().get(0).getCountOfHates());
        assertEquals(1, movies.getContent().get(1).getCountOfHates());
        assertEquals(0, movies.getContent().get(2).getCountOfHates());
    }
}