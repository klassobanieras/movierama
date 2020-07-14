package com.tsompos.movierama.service;

import com.tsompos.movierama.entity.*;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieRecommendationServiceTest {

    private final MovieRecommendationRepository movieRecommendationRepository = Mockito.mock(MovieRecommendationRepository.class);
    private final MovieRecommendationService serviceUnderTest = new MovieRecommendationService(movieRecommendationRepository);


    @Test
    void addReaction() {
        //given
        Optional<MovieRecommendation> movieRecommendation = ofNullable(
            MovieRecommendation.builder().movieId(12345L).title("aTitle").description("aDescription").publishedBy("123").build());

        when(movieRecommendationRepository.findById(any())).thenReturn(movieRecommendation);
        //when
        serviceUnderTest.addReaction(12345L, "123456", Reaction.LIKE);
        serviceUnderTest.addReaction(12345L, "1234567", Reaction.HATE);
        //then
        verify(movieRecommendationRepository).incrementLikes(12345L);
        verify(movieRecommendationRepository).incrementHates(12345L);
    }

    @Test
    void removeReaction() {
        //given
        Set<UserReaction> userReactions = new HashSet<>();
        userReactions.add(UserReaction.builder().username("123456").reaction(Reaction.LIKE).build());
        userReactions.add(UserReaction.builder().username("1234567").reaction(Reaction.LIKE).build());
        Optional<MovieRecommendation> movieRecommendation = ofNullable(MovieRecommendation.builder()
            .movieId(12345L)
            .title("aTitle")
            .description("aDescription")
            .userReactions(userReactions)
            .build());

        when(movieRecommendationRepository.findById(any())).thenReturn(movieRecommendation);
        //when
        serviceUnderTest.removeReaction(12345L, "123456");
        //then
        assertEquals(Set.of(UserReaction.builder().username("1234567").reaction(Reaction.LIKE).build()),
            movieRecommendation.get().getUserReactions());
        verify(movieRecommendationRepository).decrementLikes(12345L);
    }

    @Test
    void removeReactionWhenMovieDoesNotExistShouldThrow() {
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.removeReaction(12345L, "123456"));
    }

    @Test
    void removeReactionWhenNoReactionShouldThrow() {
        when(movieRecommendationRepository.findById(any())).thenReturn(
            ofNullable(MovieRecommendation.builder().title("aTitle").description("aDescription").build()));
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.removeReaction(12345L, "123456"));
    }

    @Test
    void switchReactionLikeToHate() {
        //given
        Set<UserReaction> userReactions = new HashSet<>();
        userReactions.add(UserReaction.builder().username("123456").reaction(Reaction.LIKE).build());
        userReactions.add(UserReaction.builder().username("1234567").reaction(Reaction.LIKE).build());
        Optional<MovieRecommendation> movieRecommendation = ofNullable(MovieRecommendation.builder()
            .movieId(12345L)
            .title("aTitle")
            .description("aDescription")
            .countOfLikes(2)
            .userReactions(userReactions)
            .build());

        when(movieRecommendationRepository.findById(any())).thenReturn(movieRecommendation);
        //when
        serviceUnderTest.switchReaction(12345L, "123456");
        //then
        assertEquals(Set.of(UserReaction.builder().username("123456").reaction(Reaction.HATE).build(),
            UserReaction.builder().username("1234567").reaction(Reaction.LIKE).build()),
            movieRecommendation.get().getUserReactions());
        verify(movieRecommendationRepository).decrementLikes(12345L);
        verify(movieRecommendationRepository).incrementHates(12345L);
    }

    @Test
    void switchReactionHateToLike() {
        //given
        Set<UserReaction> userReactions = new HashSet<>();
        userReactions.add(UserReaction.builder().username("123456").reaction(Reaction.HATE).build());
        userReactions.add(UserReaction.builder().username("1234567").reaction(Reaction.LIKE).build());
        Optional<MovieRecommendation> movieRecommendation = ofNullable(MovieRecommendation.builder()
            .movieId(12345L)
            .title("aTitle")
            .description("aDescription")
            .countOfLikes(2)
            .userReactions(userReactions)
            .build());

        when(movieRecommendationRepository.findById(any())).thenReturn(movieRecommendation);
        //when
        serviceUnderTest.switchReaction(12345L, "123456");
        //then
        assertEquals(Set.of(UserReaction.builder().username("123456").reaction(Reaction.HATE).build(),
            UserReaction.builder().username("1234567").reaction(Reaction.LIKE).build()),
            movieRecommendation.get().getUserReactions());
        verify(movieRecommendationRepository).decrementHates(12345L);
        verify(movieRecommendationRepository).incrementLikes(12345L);
    }
}