package com.tsompos.movierama.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.UserReaction;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.Set;

@ProjectedPayload
public interface MovieProjection {

    Long getMovieId();

    String getTitle();

    String getDescription();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Set<UserReaction> getUserReactions();

    long getCountOfLikes();

    long getCountOfHates();

    default String getCurrentUserReaction() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "NONE";
        }
        Jwt principal = (Jwt) authentication.getPrincipal();
        return getUserReactions().stream()
            .filter(userReaction -> userReaction.getUsername().equals(principal.getClaimAsString("username")))
            .findFirst()
            .map(UserReaction::getReaction)
            .map(Reaction::name)
            .orElse("NONE");
    }

    String getPublishedBy();

    LocalDateTime getPublishedDate();
}
