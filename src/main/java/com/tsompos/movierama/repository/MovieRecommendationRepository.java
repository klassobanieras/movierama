package com.tsompos.movierama.repository;

import com.tsompos.movierama.dto.MovieProjection;
import com.tsompos.movierama.entity.MovieRecommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, Long> {

    Page<MovieProjection> findAllBy(Pageable pageable);


    Page<MovieProjection> findAllByPublishedBy(String userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfLikes = movie.countOfLikes + 1 WHERE movie.movieId = :movieId")
    void incrementLikes(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfLikes = movie.countOfLikes + 1 WHERE movie.movieId= :movieId")
    void incrementHates(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfLikes = movie.countOfLikes - 1 WHERE movie.movieId = :movieId")
    void decrementLikes(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfLikes = movie.countOfLikes - 1 WHERE movie.movieId= :movieId")
    void decrementHates(@Param("movieId") Long movieId);

}
