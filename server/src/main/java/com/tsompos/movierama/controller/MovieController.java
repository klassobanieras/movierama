package com.tsompos.movierama.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.service.MovieRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    public Flux<MovieProjection> getAllMovies(@AuthenticationPrincipal Jwt principal,
                                              @SortDefault(sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String usernameOfCurrentUser = principal != null ? ofNullable(principal.getClaimAsString(USER_CLAIM)).orElse("") : "";
        return service.fetchAllMovies(usernameOfCurrentUser, pageable).map(MovieProjection::fromRecommendation);
    }

    @GetMapping(MOVIES_URL + "/{username}")
    public Flux<MovieProjection> getAllMoviesRecommendedBy(@AuthenticationPrincipal Jwt principal,
                                                           @PathVariable String username,
                                                           @SortDefault(sort = "publishedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String usernameOfCurrentUser = principal != null ? ofNullable(principal.getClaimAsString(USER_CLAIM)).orElse("") : "";
        return service.fetchAllMoviesOfUser(usernameOfCurrentUser, username, pageable)
                      .map(MovieProjection::fromRecommendation);
    }

    @PostMapping(MOVIES_URL)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieRecommendation> createMovieRecommendation(@AuthenticationPrincipal Jwt principal,
                                                               @RequestBody CreateMovieRecommendation command) {
        var movieRecommendation = MovieRecommendation.builder()
                                                     .title(command.title())
                                                     .description(command.description())
                                                     .build();
        return service.save(movieRecommendation, principal.getClaimAsString(USER_CLAIM));
    }

    @PostMapping(MOVIES_URL + "/{movieId}/reaction/{reaction}")
    public Mono<ReactionResponse> setReaction(@AuthenticationPrincipal Jwt principal,
                                              @PathVariable UUID movieId,
                                              @PathVariable String reaction) {
        return service.react(movieId, principal.getClaimAsString(USER_CLAIM), Reaction.fromInput(reaction))
                      .then(Mono.just(new ReactionResponse(true)))
                      .onErrorReturn(new ReactionResponse(false));
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

    record MovieProjection(String title, String description, UUID publishedById, LocalDateTime publishedDate, long likes, long hates) {
        public static MovieProjection fromRecommendation(MovieRecommendation movieRecommendation) {
            return new MovieProjection(movieRecommendation.getTitle(), movieRecommendation.getDescription(), movieRecommendation.getPublisherId(), movieRecommendation.getPublishedDate(), movieRecommendation.getLikes(), movieRecommendation.getHates());
        }
    }
}
