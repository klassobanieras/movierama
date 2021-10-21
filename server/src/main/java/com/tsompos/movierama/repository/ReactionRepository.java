package com.tsompos.movierama.repository;

import com.tsompos.movierama.entity.MovieRecommendation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ReactionRepository extends Repository<MovieRecommendation, UUID> {
    @Modifying
    @Transactional
    @Query("""
            UPDATE MovieRecommendation movie
            SET movie.countOfLikes = movie.countOfLikes + 1
            WHERE movie.id = :movieId
            """)
    void incrementLikes(@Param("movieId") UUID movieId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE MovieRecommendation movie
            SET movie.countOfHates = movie.countOfHates + 1
            WHERE movie.id= :movieId
            """)
    void incrementHates(@Param("movieId") UUID movieId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE MovieRecommendation movie
            SET movie.countOfLikes = movie.countOfLikes - 1
            WHERE movie.id = :movieId
            """)
    void decrementLikes(@Param("movieId") UUID movieId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE MovieRecommendation movie
            SET movie.countOfHates = movie.countOfHates - 1
            WHERE movie.id= :movieId
            """)
    void decrementHates(@Param("movieId") UUID movieId);
}
