package com.tsompos.movierama.repository;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MovieRecommendationRepositoryTest {

    @Autowired
    private MovieRecommendationRepository movieRecommendationRepository;
    private MovieRecommendation.MovieRecommendationBuilder movieRecommendationBuilder;

    @BeforeEach
    void setUp() {
        movieRecommendationBuilder = MovieRecommendation
                .builder()
                .title("aTitle")
                .description("a description")
                .usersThatLiked(Set.of(User.builder().userName("me").build(), User.builder().userName("you").build()))
                .countOfLikes(2L)
                .publishedBy("publisher");
    }

    @Test
    void findAllAnonymousUser() {
        movieRecommendationRepository.save(movieRecommendationBuilder.build());

        var movies = movieRecommendationRepository.findAll("", Pageable.unpaged());
        assertThat(movies.getContent().size()).isOne();
        assertThat(movies.getContent().get(0).getCountOfLikes()).isEqualTo(2);
    }

    @Test
    void findAllAndUserReactionOf() {
        movieRecommendationRepository.save(movieRecommendationBuilder.build());

        var movies = movieRecommendationRepository.findAll("aUserThatHasNotReacted", Pageable.unpaged());
        assertEquals(1, movies.getContent().size());
        assertEquals(2, movies.getContent().get(0).getCountOfLikes());
        assertEquals(Reaction.NONE, movies.getContent().get(0).getUsersReaction());
    }

    @Test
    void findAllSortByLikes() {
        movieRecommendationRepository.save(movieRecommendationBuilder.countOfLikes(2L).build());
        movieRecommendationRepository.save(movieRecommendationBuilder.title("for sure another title")
                                                                     .countOfLikes(1L)
                                                                     .build());
        movieRecommendationRepository.save(movieRecommendationBuilder.title("another title").countOfLikes(0L).build());
        PageRequest countOfLikesDesc = PageRequest.of(0, 10, Sort.by("countOfLikes").descending());
        var movies =
                movieRecommendationRepository.findAll("", countOfLikesDesc);

        assertThat(movies.getContent().size()).isEqualTo(3);
        assertThat(movies.getContent().get(0).getCountOfLikes()).isEqualTo(2);
        assertThat(movies.getContent().get(1).getCountOfLikes()).isEqualTo(1);
        assertThat(movies.getContent().get(2).getCountOfLikes()).isEqualTo(0);
    }

    @Test
    void findAllSortByHates() {
        movieRecommendationRepository.save(movieRecommendationBuilder.countOfHates(0L).build());
        movieRecommendationRepository.save(movieRecommendationBuilder.title("for sure another title")
                                                                     .countOfHates(1L)
                                                                     .build());
        movieRecommendationRepository.save(movieRecommendationBuilder.title("another title").countOfHates(2L).build());

        PageRequest countOfHatesDesc = PageRequest.of(0, 10, Sort.by("countOfHates").descending());
        var movies =
                movieRecommendationRepository.findAll("", countOfHatesDesc);

        assertEquals(3, movies.getContent().size());
        assertThat(movies.getContent().size()).isEqualTo(3);
        assertThat(movies.getContent().get(0).getCountOfHates()).isEqualTo(2);
        assertThat(movies.getContent().get(1).getCountOfHates()).isEqualTo(1);
        assertThat(movies.getContent().get(2).getCountOfHates()).isEqualTo(0);
    }

}
