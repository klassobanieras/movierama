package com.tsompos.movierama.service;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;
import com.tsompos.movierama.error.MultipleReactionsException;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import com.tsompos.movierama.repository.ReactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MovieRecommendationServiceTest {

    @MockBean
    private MovieRecommendationRepository movieRecommendationRepository;
    @MockBean
    private ReactionRepository reactionRepository;
    @Autowired
    private MovieRecommendationService serviceUnderTest;
    private static final UUID MOVIE_ID = UUID.randomUUID();
    private static final String USERNAME = "best-username";
    private final User user = User.builder().userName(USERNAME).build();
    private final MovieRecommendation noReactionMovie =
            MovieRecommendation.builder()
                               .id(MOVIE_ID)
                               .title("aTitle")
                               .description("aDescription")
                               .publishedBy("123")
                               .build();
    private final MovieRecommendation likedMovie =
            MovieRecommendation.builder()
                               .id(MOVIE_ID)
                               .title("aTitle")
                               .description("aDescription")
                               .publishedBy("123")
                               .countOfLikes(1L)
                               .usersThatLiked(new HashSet<>(Set.of(user)))
                               .build();
    private final MovieRecommendation hatedMovie =
            MovieRecommendation.builder()
                               .id(MOVIE_ID)
                               .title("aTitle")
                               .description("aDescription")
                               .publishedBy("123")
                               .countOfHates(1L)
                               .usersThatHated(new HashSet<>(Set.of(user)))
                               .build();

    @Test
    void likeAMovie() {
        //given
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(noReactionMovie));
        //when
        serviceUnderTest.react(MOVIE_ID, user, Reaction.LIKE);
        //then
        verify(reactionRepository).incrementLikes(MOVIE_ID);
    }

    @Test
    void likeALikedMovie() {
        //given
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(likedMovie));
        //when
        assertThrows(MultipleReactionsException.class, () -> serviceUnderTest.react(MOVIE_ID, user, Reaction.LIKE));
        //then
        verify(reactionRepository, times(0)).incrementLikes(MOVIE_ID);
    }

    @Test
    void likeAHatedMovie() {
        //given
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(hatedMovie));
        //when
        serviceUnderTest.react(MOVIE_ID, user, Reaction.LIKE);
        //then
        verify(reactionRepository).decrementHates(MOVIE_ID);
        verify(reactionRepository).incrementLikes(MOVIE_ID);
    }

    @Test
    void hateAMovie() {
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(noReactionMovie));
        //when
        serviceUnderTest.react(MOVIE_ID, user, Reaction.HATE);
        //then
        verify(reactionRepository).incrementHates(MOVIE_ID);
    }

    @Test
    void hateALikedMovie() {
        //given
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(likedMovie));
        //when
        serviceUnderTest.react(MOVIE_ID, user, Reaction.HATE);
        //then
        verify(reactionRepository).decrementLikes(MOVIE_ID);
        verify(reactionRepository).incrementHates(MOVIE_ID);
    }

    @Test
    void hateAHatedMovie() {
        //given
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(hatedMovie));
        //when
        assertThrows(MultipleReactionsException.class, () -> serviceUnderTest.react(MOVIE_ID, user, Reaction.HATE));
        //then
        verify(reactionRepository, times(0)).incrementHates(MOVIE_ID);
    }

    @Test
    void removeReactionFromAMovie() {
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(noReactionMovie));
        serviceUnderTest.react(MOVIE_ID, user, Reaction.NONE);
        //then
        verify(reactionRepository, times(0)).incrementHates(MOVIE_ID);
        verify(reactionRepository, times(0)).incrementLikes(MOVIE_ID);
        verify(reactionRepository, times(0)).decrementHates(MOVIE_ID);
        verify(reactionRepository, times(0)).decrementLikes(MOVIE_ID);
    }

    @Test
    void removeReactionFromALikedMovie() {
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(likedMovie));
        serviceUnderTest.react(MOVIE_ID, user, Reaction.NONE);
        //then
        verify(reactionRepository).decrementLikes(MOVIE_ID);
        verify(reactionRepository, times(0)).incrementHates(MOVIE_ID);
    }

    @Test
    void removeReactionFromAHatedMovie() {
        when(movieRecommendationRepository.findById(any())).thenReturn(Optional.of(hatedMovie));
        serviceUnderTest.react(MOVIE_ID, user, Reaction.NONE);
        //then
        verify(reactionRepository).decrementHates(MOVIE_ID);
        verify(reactionRepository, times(0)).incrementLikes(MOVIE_ID);
    }
}