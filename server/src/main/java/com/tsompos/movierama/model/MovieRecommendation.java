package com.tsompos.movierama.model;

import com.tsompos.movierama.error.OwnMovieRecommendation;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.util.Assert.notNull;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document("movierecommendation")
public final class MovieRecommendation {

    public static final String HATES_FIELD = "hates";
    public static final String LIKES_FIELD = "likes";

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    @TextIndexed
    private String title;
    private String description;
    private UUID publisherId;
    @CreatedDate
    private LocalDateTime publishedDate;
    @Field(value = LIKES_FIELD)
    private long likes;
    @Field(value = HATES_FIELD)
    private long hates;
    @Transient
    private Reaction currentUserReaction;

    @Builder
    private MovieRecommendation(UUID id, String title, String description, UUID publisherId, LocalDateTime publishedDate, Long likes, Long hates, Reaction currentUserReaction) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID());
        this.title = title;
        this.description = description;
        this.publisherId = publisherId;
        this.publishedDate = publishedDate;
        this.likes = Objects.requireNonNullElse(likes, 0L);
        this.hates = Objects.requireNonNullElse(hates, 0L);
        this.currentUserReaction = currentUserReaction;

        notNull(title, "The title should not be empty");
        notNull(description, "The description should not be empty");
    }

    public MovieRecommendation validate(UUID userId) throws OwnMovieRecommendation {
        if (this.isPublishedByUser(userId)) {
            throw new OwnMovieRecommendation("Cannot react to your own movie.");
        }
        return this;
    }

    private boolean isPublishedByUser(UUID userId) {
        return publisherId.equals(userId);
    }

    public boolean userAlreadyHatedTheMovie() {
        return currentUserReaction == Reaction.HATE;
    }

    public boolean userAlreadyLikedTheMovie() {
        return currentUserReaction == Reaction.LIKE;
    }

    public MovieRecommendation setCurrentUserReaction(User user) {
        this.currentUserReaction = user.getReactionForMovie(this);
        return this;
    }

}
