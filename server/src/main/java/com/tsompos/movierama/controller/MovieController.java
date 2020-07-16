package com.tsompos.movierama.controller;

import com.tsompos.movierama.dto.CreateMovie;
import com.tsompos.movierama.dto.MovieProjection;
import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.service.MovieRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static com.tsompos.movierama.config.ApplicationConfiguration.MOVIES_URL;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class MovieController {

    private final MovieRecommendationService movieRecommendationService;

    @GetMapping(MOVIES_URL)
    public Page<MovieProjection> getAllMovies(@PageableDefault(page = 0, size = 20)
    @SortDefault.SortDefaults({@SortDefault(sort = "publishedDate", direction = Sort.Direction.DESC)}) Pageable pageable) {
        return movieRecommendationService.fetchAllMovies(pageable);
    }

    @GetMapping(MOVIES_URL + "/{username}")
    public Page<MovieProjection> getAllMovies(@PathVariable String username, @PageableDefault(page = 0, size = 20)
    @SortDefault.SortDefaults({@SortDefault(sort = "publishedDate", direction = Sort.Direction.DESC)}) Pageable pageable) {
        return movieRecommendationService.fetchAllMoviesOfUser(username, pageable);
    }

    @PostMapping(MOVIES_URL)
    @ResponseStatus(HttpStatus.CREATED)
    public MovieRecommendation createMovieRecommendation(@AuthenticationPrincipal Jwt principal,
        @RequestBody CreateMovie createMovie) {
        log.info(principal.getClaims().toString());
        log.info(principal.getSubject());
        return movieRecommendationService.save(createMovie, principal.getClaimAsString("email"));
    }

    @PostMapping(MOVIES_URL + "/{movieId}/reaction/{reaction}")
    public void setReaction(@AuthenticationPrincipal Jwt principal, @PathVariable Long movieId, @PathVariable String reaction) {
        movieRecommendationService.addReaction(movieId, principal.getClaimAsString("email"),
            Reaction.valueOf(reaction.toUpperCase()));
    }

    @DeleteMapping(MOVIES_URL + "/{movieId}/reaction")
    public void removeReaction(@AuthenticationPrincipal Jwt principal, @PathVariable Long movieId) {
        movieRecommendationService.removeReaction(movieId, principal.getClaimAsString("email"));
    }

    @PutMapping(MOVIES_URL + "/{movieId}/reaction")
    public void switchReaction(@AuthenticationPrincipal Jwt principal, @PathVariable Long movieId) {
        movieRecommendationService.switchReaction(movieId, principal.getClaimAsString("email"));
    }

}
