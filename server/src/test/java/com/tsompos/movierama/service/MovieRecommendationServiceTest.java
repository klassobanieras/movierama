package com.tsompos.movierama.service;

import com.tsompos.movierama.TestContainerDBTest;
import com.tsompos.movierama.error.MultipleReactionsException;
import com.tsompos.movierama.model.MovieReaction;
import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class MovieRecommendationServiceTest extends TestContainerDBTest {
    @Autowired
    private MovieRecommendationService serviceUnderTest;

    static final UUID LIKED_MOVIE_ID = UUID.randomUUID();
    static final UUID HATED_MOVIE_ID = UUID.randomUUID();
    static final String SIMPLE_USER_NAME = "Joe";
    static final String REACTED_USER_NAME = "Reacted Joe";
    static final String PUBLISHER_NAME = "publisher name";

    final User publisher = User.builder().userName(PUBLISHER_NAME).build();
    final User user = User.builder().userName(SIMPLE_USER_NAME).build();
    final User reactedUser = User.builder().userName(REACTED_USER_NAME)
                                 .movieReactions(Set.of(MovieReaction.builder().movieId(LIKED_MOVIE_ID).reaction(Reaction.LIKE)
                                                                     .build(), MovieReaction.builder().movieId(HATED_MOVIE_ID)
                                                                                            .reaction(Reaction.HATE).build()))
                                 .build();
    final MovieRecommendation likedMovie = MovieRecommendation.builder().id(LIKED_MOVIE_ID).title("A Liked Movie")
                                                              .description("aDescription").publisherId(publisher.getId()).likes(2L)
                                                              .build();
    final MovieRecommendation hatedMovie = MovieRecommendation.builder().id(HATED_MOVIE_ID).title("A Hated Movie")
                                                              .description("aDescription").publisherId(publisher.getId()).hates(2L)
                                                              .build();

    private Mono<Void> beforeEach() {
        var beforeEach = Flux.merge(userRepository.saveAll(Flux.just(publisher, user, reactedUser)), movieRecommendationRepository.saveAll(Flux.just(likedMovie, hatedMovie)));
        return cleanDB().thenMany(beforeEach).log().then();
    }

    @Test
    void likeAMovie() {
        //given
        var setup = beforeEach().then(serviceUnderTest.react(LIKED_MOVIE_ID, SIMPLE_USER_NAME, Reaction.LIKE));
        //when
        var find = Mono.zip(movieRecommendationRepository.findById(LIKED_MOVIE_ID), userRepository.findByUserName(SIMPLE_USER_NAME));
        //then
        StepVerifier.create(Mono.from(setup).then(find))
                    .consumeNextWith(zip -> {
                        //movieRecommendation
                        assertThat(zip.getT1()).isNotNull()
                                               .extracting(MovieRecommendation::getLikes)
                                               .isEqualTo(3L);
                        //user
                        assertThat(zip.getT2()).isNotNull().extracting(User::getMovieReactions).extracting(Set::size).isEqualTo(1);
                    })
                    .verifyComplete();
    }

    @Test
    void hateMovie() {
        //given
        var setup = beforeEach().then(serviceUnderTest.react(HATED_MOVIE_ID, SIMPLE_USER_NAME, Reaction.HATE));
        //when
        var find = Mono.zip(movieRecommendationRepository.findById(HATED_MOVIE_ID), userRepository.findByUserName(SIMPLE_USER_NAME));
        //then
        StepVerifier.create(Mono.from(setup).then(find))
                    .consumeNextWith(zip -> {
                        //movieRecommendation
                        assertThat(zip.getT1()).isNotNull()
                                               .extracting(MovieRecommendation::getHates)
                                               .isEqualTo(3L);
                        //user
                        assertThat(zip.getT2()).isNotNull().extracting(User::getMovieReactions).extracting(Set::size).isEqualTo(1);
                    })
                    .verifyComplete();
    }

    @Test
    void hateALikedMovie() {
        //given
        var setup = beforeEach().then(serviceUnderTest.react(LIKED_MOVIE_ID, REACTED_USER_NAME, Reaction.HATE));
        //when
        var find = Mono.zip(movieRecommendationRepository.findById(LIKED_MOVIE_ID), userRepository.findByUserName(REACTED_USER_NAME));
        //then
        StepVerifier.create(Mono.from(setup).then(find))
                    .consumeNextWith(zip -> {
                        //movieRecommendation
                        assertThat(zip.getT1()).isNotNull()
                                               .extracting(MovieRecommendation::getLikes)
                                               .isEqualTo(1L);
                        //user
                        assertThat(zip.getT2()).isNotNull().extracting(User::getMovieReactions).extracting(Set::size).isEqualTo(1);
                    })
                    .verifyComplete();
    }

    @Test
    void likeAHatedMovie() {
        //given
        var setup = beforeEach().then(serviceUnderTest.react(HATED_MOVIE_ID, REACTED_USER_NAME, Reaction.LIKE));
        //when
        var find = Mono.zip(movieRecommendationRepository.findById(HATED_MOVIE_ID), userRepository.findByUserName(REACTED_USER_NAME));
        //then
        StepVerifier.create(Mono.from(setup).then(find))
                    .consumeNextWith(zip -> {
                        //movieRecommendation
                        assertThat(zip.getT1()).isNotNull()
                                               .extracting(MovieRecommendation::getHates)
                                               .isEqualTo(1L);
                        //user
                        assertThat(zip.getT2()).isNotNull().extracting(User::getMovieReactions).extracting(Set::size).isEqualTo(1);
                    })
                    .verifyComplete();
    }

    @Test
    void likeALikedMovie() {
        //given
        var setup = beforeEach().then(serviceUnderTest.react(LIKED_MOVIE_ID, REACTED_USER_NAME, Reaction.LIKE));
        //when
        var find = Mono.zip(movieRecommendationRepository.findById(LIKED_MOVIE_ID), userRepository.findByUserName(REACTED_USER_NAME));
        //then
        StepVerifier.create(Mono.from(setup).then(find)).expectError(MultipleReactionsException.class).verify();
    }

    @Test
    void hateAHatedMovie() {
        //given
        var setup = beforeEach().then(serviceUnderTest.react(HATED_MOVIE_ID, REACTED_USER_NAME, Reaction.HATE));
        //when
        var find = Mono.zip(movieRecommendationRepository.findById(HATED_MOVIE_ID), userRepository.findByUserName(REACTED_USER_NAME));
        //then
        StepVerifier.create(Mono.from(setup).then(find)).expectError(MultipleReactionsException.class).verify();
    }
}