package com.tsompos.movierama.repository;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ReactionRepositoryTest {

    @Autowired
    private MovieRecommendationRepository movieRecommendationRepository;
    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private EntityManager entityManager;
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
    void testIncrementLikes() {
        MovieRecommendation movieRecommendation = movieRecommendationRepository.save(movieRecommendationBuilder.build());
        reactionRepository.incrementLikes(movieRecommendation.getId());
        entityManager.clear();

        long countOfLikes = movieRecommendationRepository.findById(movieRecommendation.getId())
                                                         .orElseThrow()
                                                         .getCountOfLikes();
        assertThat(countOfLikes).isEqualTo(3);
    }

    @Test
    void testIncrementHates() {
        MovieRecommendation movieRecommendation = movieRecommendationRepository.save(movieRecommendationBuilder.build());
        reactionRepository.incrementHates(movieRecommendation.getId());
        entityManager.clear();

        long countOfHates = movieRecommendationRepository.findById(movieRecommendation.getId())
                                                         .orElseThrow()
                                                         .getCountOfHates();
        assertThat(countOfHates).isEqualTo(1);
    }

    @Test
    void testDecrementLikes() {

        MovieRecommendation movieRecommendation = movieRecommendationRepository.save(movieRecommendationBuilder.build());
        reactionRepository.decrementLikes(movieRecommendation.getId());
        entityManager.clear();

        long countOfLikes = movieRecommendationRepository.findById(movieRecommendation.getId())
                                                         .orElseThrow()
                                                         .getCountOfLikes();
        assertThat(countOfLikes).isEqualTo(1);
    }

    @Test
    void testDecrementHates() {
        MovieRecommendation movieRecommendation = movieRecommendationRepository.save(movieRecommendationBuilder.countOfHates(1L)
                                                                                                               .build());
        reactionRepository.decrementHates(movieRecommendation.getId());
        entityManager.clear();

        long countOfHates = movieRecommendationRepository.findById(movieRecommendation.getId())
                                                         .orElseThrow()
                                                         .getCountOfHates();
        assertThat(countOfHates).isEqualTo(0);
    }
}
