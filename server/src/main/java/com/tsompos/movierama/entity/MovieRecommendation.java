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
    @Column(name = "ID")
    private Long id;
    @Column(unique = true, nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(name = "COUNT_OF_HATES")
    private long countOfHates = 0;
    @Column(name = "COUNT_OF_LIKES")
    private long countOfLikes = 0;
    @ElementCollection
    private Set<UserReaction> userReactions;
    @Column(name = "PUBLISHED_BY")
    private String publishedBy;
    @CreatedDate
    @Column(name = "PUBLISHED_DATE")
    private LocalDateTime publishedDate;

    @Builder
    private MovieRecommendation(Long id, String title, String description, long countOfHates, long countOfLikes,
                                Set<UserReaction> userReactions, String publishedBy, LocalDateTime publishedDate) {
        this.id = id;
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
