package com.tsompos.movierama.service;

import com.tsompos.movierama.dto.CreateMovie;
import com.tsompos.movierama.dto.MovieProjection;
import com.tsompos.movierama.entity.*;
import com.tsompos.movierama.error.MultipleReactionsException;
import com.tsompos.movierama.error.OwnMovieRecommendation;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieRecommendationService {

    private final MovieRecommendationRepository movieRecommendationRepository;

    public Page<MovieProjection> fetchAllMovies(Pageable pageable) {
        return movieRecommendationRepository.findAllBy(pageable);
    }

    public Page<MovieProjection> fetchAllMoviesOfUser(String username, Pageable pageable) {
        return movieRecommendationRepository.findAllByPublishedBy(username, pageable);
    }

    public MovieRecommendation save(CreateMovie createMovie, String username) {
        MovieRecommendation movieRecommendation = MovieRecommendation.builder()
            .title(createMovie.getTitle())
            .description(createMovie.getDescription())
            .publishedBy(username)
            .build();

        return Try.of(() -> movieRecommendationRepository.save(movieRecommendation))
            .getOrElseThrow(() -> new EntityExistsException("The Movie already exists"));
    }

    public void addReaction(Long movieId, String username, Reaction reaction) {
        MovieRecommendation movieRecommendation =
            movieRecommendationRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException("Movie not found."));

        if (movieRecommendation.getPublishedBy().equals(username)) {
            throw new OwnMovieRecommendation("Cannot " + reaction.name().toLowerCase() + " your own movie.");
        }
        if (!movieRecommendation.getUserReactions().add(UserReaction.builder().username(username).reaction(reaction).build())) {
            throw new MultipleReactionsException("Cannot " + reaction.name().toLowerCase() + " a movie more than once.");
        }
        movieRecommendationRepository.save(movieRecommendation);
        switch (reaction) {
            case LIKE:
                movieRecommendationRepository.incrementLikes(movieRecommendation.getMovieId());
                break;
            case HATE:
                movieRecommendationRepository.incrementHates(movieRecommendation.getMovieId());
                break;
        }
    }

    public void removeReaction(Long movieId, String username) {
        MovieRecommendation movieRecommendation =
            movieRecommendationRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException("Movie not found."));

        UserReaction userReaction = movieRecommendation.getUserReactions()
            .stream()
            .filter(reaction -> reaction.getUsername().equals(username))
            .findAny()
            .orElseThrow(() -> new EntityNotFoundException("No reaction to remove"));

        movieRecommendation.getUserReactions().remove(userReaction);

        switch (userReaction.getReaction()) {
            case LIKE:
                movieRecommendationRepository.decrementLikes(movieRecommendation.getMovieId());
                break;
            case HATE:
                movieRecommendationRepository.decrementHates(movieRecommendation.getMovieId());
                break;
        }
    }

    public void switchReaction(Long movieId, String username) {
        MovieRecommendation movieRecommendation =
            movieRecommendationRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException("Movie not found."));

        UserReaction userReaction = movieRecommendation.getUserReactions()
            .stream()
            .filter(reaction -> reaction.getUsername().equals(username))
            .findAny()
            .orElseThrow(() -> new EntityNotFoundException("No reaction to change"));

        movieRecommendation.getUserReactions().remove(userReaction);

        switch (userReaction.getReaction()) {
            case LIKE:
                movieRecommendation.getUserReactions().add(UserReaction.builder().username(username).reaction(Reaction.HATE).build());
                movieRecommendationRepository.decrementLikes(movieId);
                movieRecommendationRepository.incrementHates(movieId);
                break;
            case HATE:
                movieRecommendation.getUserReactions().add(UserReaction.builder().username(username).reaction(Reaction.LIKE).build());
                movieRecommendationRepository.decrementHates(movieId);
                movieRecommendationRepository.incrementLikes(movieId);
                break;
        }
    }
}
