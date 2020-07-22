package com.tsompos.movierama.repository;

import com.tsompos.movierama.dto.MovieProjection;
import com.tsompos.movierama.entity.MovieRecommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, Long> {

    @Query(value =
        "SELECT m.* , cur.reaction AS CURRENT_USER_REACTION " +
            "FROM MOVIE_RECOMMENDATION m " +
                  "LEFT JOIN ( SELECT ur.MOVIE_RECOMMENDATION_ID, ur.reaction " +
                                "FROM MOVIE_RECOMMENDATION_USER_REACTIONS ur " +
                                "WHERE ur.username = :email" +
                            ") as cur ON cur.MOVIE_RECOMMENDATION_ID = m.ID "
        , countQuery = "SELECT COUNT(*) FROM MOVIE_RECOMMENDATION"
        , nativeQuery = true)
    Page<MovieProjection> findAll(@Param("email") String email, Pageable pageable);

    @Query(value =
        "SELECT m.* , cur.reaction AS CURRENT_USER_REACTION " +
            "FROM MOVIE_RECOMMENDATION m " +
                "LEFT JOIN ( SELECT ur.MOVIE_RECOMMENDATION_ID, ur.reaction " +
                                "FROM MOVIE_RECOMMENDATION_USER_REACTIONS ur " +
                                "WHERE ur.username = :emailOfCurrentUser" +
                            ") as cur ON cur.MOVIE_RECOMMENDATION_ID = m.ID " +
            "WHERE m.PUBLISHED_BY = :email"
        , countQuery = "SELECT COUNT(*) FROM MOVIE_RECOMMENDATION m WHERE m.PUBLISHED_BY = :email"
        , nativeQuery = true)
    Page<MovieProjection> findAllPublishedBy(@Param("emailOfCurrentUser") String emailOfCurrentUser, @Param("email") String email, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfLikes = movie.countOfLikes + 1 WHERE movie.id = :movieId")
    void incrementLikes(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfHates = movie.countOfHates + 1 WHERE movie.id= :movieId")
    void incrementHates(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfLikes = movie.countOfLikes - 1 WHERE movie.id = :movieId")
    void decrementLikes(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieRecommendation movie SET movie.countOfHates = movie.countOfHates - 1 WHERE movie.id= :movieId")
    void decrementHates(@Param("movieId") Long movieId);

}
