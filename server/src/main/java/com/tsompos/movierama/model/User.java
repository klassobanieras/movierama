package com.tsompos.movierama.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document("user")
public class User {

    public static final String MOVIE_REACTIONS_FIELD = "movieReactions";
    @Id
    @NonNull
    private UUID id;
    @EqualsAndHashCode.Include
    @Indexed
    private String userName;
    @Field(value = MOVIE_REACTIONS_FIELD)
    private Set<MovieReaction> movieReactions;

    @Builder(toBuilder = true)
    private User(UUID id, String userName, Set<MovieReaction> movieReactions) {
        this.id = Objects.requireNonNullElse(id, UUID.randomUUID());
        this.userName = Objects.requireNonNull(userName, "Username cannot be null");
        this.movieReactions = Objects.requireNonNullElse(movieReactions, new HashSet<>());
    }

    public Reaction getReactionForMovie(MovieRecommendation movieRecommendation) {
        return movieReactions.stream().filter(reactedMovie -> reactedMovie.getMovieId().equals(movieRecommendation.getId()))
                             .map(MovieReaction::getReaction).findAny().orElse(Reaction.NONE);
    }
}
