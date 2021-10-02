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

    @Query("select m.id as id, " +
            "m.title as title, " +
            "m.description as description, " +
            "m.countOfHates as countOfHates, " +
            "m.countOfLikes as countOfLikes, " +
            "m.publishedBy as publishedBy, " +
            "m.publishedDate as publishedDate, " +
            "case when l.userName is not null then 'LIKE' when h.userName is not null then 'HATE' else 'NONE' end as usersReaction " +
            "from MovieRecommendation m " +
            "left join m.usersThatLiked as l on (l.userName = :username) " +
            "left join m.usersThatHated as h on (h.userName = :username) ")
    Page<MovieWithReaction> findAll(String username, Pageable pageable);

    @Query("select m.id as id, " +
            "m.title as title, " +
            "m.description as description, " +
            "m.countOfHates as countOfHates, " +
            "m.countOfLikes as countOfLikes, " +
            "m.publishedBy as publishedBy, " +
            "m.publishedDate as publishedDate, " +
            "case when l.userName is not null then 'LIKE' when h.userName is not null then 'HATE' else 'NONE' end as usersReaction " +
            "from MovieRecommendation m " +
            "left join m.usersThatLiked as l on (l.userName = :username) " +
            "left join m.usersThatHated as h on (h.userName = :username) " +
            "where m.publishedBy = :publishedBy ")
    Page<MovieWithReaction> findAllPublishedBy(String username, String publishedBy, Pageable pageable);

    @Query("select distinct m " +
            "from MovieRecommendation as m " +
            "left join fetch m.usersThatHated " +
            "left join fetch m.usersThatLiked where m.id = :id ")
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
