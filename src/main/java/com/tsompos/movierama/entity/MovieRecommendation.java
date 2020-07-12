package com.tsompos.movierama.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.util.Assert.notNull;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public final class MovieRecommendation {

    @Id
    @GeneratedValue
    private Long movieId;
    @Column(unique = true)
    private String title;
    private String description;
    private long countOfHates;
    private long countOfLikes;
    @ElementCollection
    private Set<UserReaction> userReactions;
    private String publishedBy;
    @CreatedDate
    private LocalDateTime publishedDate;

    @Builder
    private MovieRecommendation(Long movieId, String title, String description, long countOfHates, long countOfLikes,
        Set<UserReaction> userReactions, String publishedBy, LocalDateTime publishedDate) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.countOfHates = countOfHates;
        this.countOfLikes = countOfLikes;
        this.userReactions = Objects.requireNonNullElse(userReactions, new HashSet<>());
        this.publishedBy = publishedBy;
        this.publishedDate = publishedDate;

        notNull(title, "The title should not be empty");
        notNull(description, "The description should not be empty");
    }
}
