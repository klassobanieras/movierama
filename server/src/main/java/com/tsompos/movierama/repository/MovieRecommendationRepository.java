package com.tsompos.movierama.repository;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, UUID> {

    @Query("""
            select movie.id as id,
            movie.title as title,
            movie.description as description,
            movie.countOfHates as countOfHates,
            movie.countOfLikes as countOfLikes,
            movie.publishedBy as publishedBy,
            movie.publishedDate as publishedDate,
            case when l.userName is not null then 'LIKE' when h.userName is not null then 'HATE' else 'NONE' end as usersReaction
            from MovieRecommendation movie
            left join movie.usersThatLiked as l on (l.userName = :username)
            left join movie.usersThatHated as h on (h.userName = :username)
            """)
    Page<MovieWithReaction> findAll(String username, Pageable pageable);

    @Query("""
            select movie.id as id,
            movie.title as title,
            movie.description as description,
            movie.countOfHates as countOfHates,
            movie.countOfLikes as countOfLikes,
            movie.publishedBy as publishedBy,
            movie.publishedDate as publishedDate,
            case when likeReaction.userName is not null then 'LIKE' when hateReaction.userName is not null then 'HATE' else 'NONE' end as usersReaction
            from MovieRecommendation movie
            left join movie.usersThatLiked as likeReaction on (likeReaction.userName = :username)
            left join movie.usersThatHated as hateReaction on (hateReaction.userName = :username)
            where movie.publishedBy = :publishedBy
            """)
    Page<MovieWithReaction> findAllPublishedBy(String username, String publishedBy, Pageable pageable);

    @Query("""
            select distinct m
            from MovieRecommendation as m
            left join fetch m.usersThatHated
            left join fetch m.usersThatLiked where m.id = :id
            """)
    Optional<MovieRecommendation> findById(UUID id);

    interface MovieWithReaction {
        UUID getId();

        String getTitle();

        String getDescription();

        Long getCountOfLikes();

        Long getCountOfHates();

        String getCurrentUserReaction();

        String getPublishedBy();

        LocalDateTime getPublishedDate();

        Reaction getUsersReaction();
    }
}
