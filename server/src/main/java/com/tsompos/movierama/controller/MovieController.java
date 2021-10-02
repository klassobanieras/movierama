package com.tsompos.movierama.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;
import com.tsompos.movierama.repository.MovieRecommendationRepository.MovieWithReaction;
import com.tsompos.movierama.service.MovieRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.tsompos.movierama.config.ApplicationConfiguration.MOVIES_URL;
import static com.tsompos.movierama.config.SecurityConfiguration.USER_CLAIM;
import static java.util.Optional.ofNullable;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class MovieController {

    private final MovieRecommendationService service;

    @GetMapping(MOVIES_URL)
    public Page<MovieWithReaction> getAllMovies(@AuthenticationPrincipal Jwt principal,
                                                @SortDefault(sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String usernameOfCurrentUser = principal != null ? ofNullable(principal.getClaimAsString(USER_CLAIM)).orElse("") : "";
        return service.fetchAllMovies(usernameOfCurrentUser, pageable);
    }

    @GetMapping(MOVIES_URL + "/{email}")
    public Page<MovieWithReaction> getAllMoviesRecommendedBy(@AuthenticationPrincipal Jwt principal,
                                                             @PathVariable String email,
                                                             @SortDefault(sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String usernameOfCurrentUser = principal != null ? ofNullable(principal.getClaimAsString(USER_CLAIM)).orElse("") : "";
        return service.fetchAllMoviesOfUser(usernameOfCurrentUser, email, pageable);
    }

    @PostMapping(MOVIES_URL)
    @ResponseStatus(HttpStatus.CREATED)
    public MovieRecommendation createMovieRecommendation(@NotNull @AuthenticationPrincipal Jwt principal,
                                                         @RequestBody CreateMovieRecommendation command) {

        var movieRecommendation = MovieRecommendation.builder()
                                                     .title(command.title())
                                                     .description(command.description())
                                                     .publishedBy(principal.getClaimAsString(USER_CLAIM))
                                                     .build();
        return service.save(movieRecommendation);
    }

    @PostMapping(MOVIES_URL + "/{movieId}/reaction/{reaction}")
    public ReactionResponse setReaction(@AuthenticationPrincipal Jwt principal,
                                        @PathVariable UUID movieId,
                                        @PathVariable String reaction) {
        User user = User.builder().userName(principal.getClaimAsString(USER_CLAIM)).build();
        service.react(movieId, user, Reaction.fromInput(reaction));
        return new ReactionResponse(true);
    }

    record ReactionResponse(boolean success) {
    }

    record CreateMovieRecommendation(String title, String description) {
        @JsonCreator
        public CreateMovieRecommendation(@JsonProperty("title") String title, @JsonProperty("description") String description) {
            this.title = title;
            this.description = description;
        }
    }
}
