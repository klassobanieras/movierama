package com.tsompos.movierama.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.springframework.util.Assert.notNull;

@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public final class MovieRecommendation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    @Column(unique = true, nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    private Long countOfHates = 0L;
    private Long countOfLikes = 0L;
    @ElementCollection
    private Set<User> usersThatLiked = new HashSet<>();
    @ElementCollection
    private Set<User> usersThatHated = new HashSet<>();
    private String publishedBy;
    @CreatedDate
    private LocalDateTime publishedDate;

    @Builder
    private MovieRecommendation(UUID id, String title, String description, Long countOfHates, Long countOfLikes,
                                Set<User> usersThatLiked, Set<User> usersThatHated, String publishedBy, LocalDateTime publishedDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.countOfHates = Objects.requireNonNullElse(countOfHates, 0L);
        this.countOfLikes = Objects.requireNonNullElse(countOfLikes, 0L);
        this.usersThatLiked = Objects.requireNonNullElse(usersThatLiked, new HashSet<>());
        this.usersThatHated = Objects.requireNonNullElse(usersThatHated, new HashSet<>());
        this.publishedBy = publishedBy;
        this.publishedDate = publishedDate;

        notNull(title, "The title should not be empty");
        notNull(description, "The description should not be empty");
    }

    public boolean userAlreadyLikedTheMovie(User user) {
        return usersThatLiked.contains(user);
    }

    public boolean userAlreadyHatedTheMovie(User user) {
        return usersThatHated.contains(user);
    }

    public boolean isPublishedBySameUser(User user) {
        return publishedBy.equals(user.getUserName());
    }
}
