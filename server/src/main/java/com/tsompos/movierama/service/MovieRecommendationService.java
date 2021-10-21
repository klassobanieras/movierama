package com.tsompos.movierama.service;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import com.tsompos.movierama.service.reaction.ReactionUseCase;
import io.vavr.control.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional
public class MovieRecommendationService {

    private final MovieRecommendationRepository movieRecommendationRepository;
    private final Map<Reaction, ReactionUseCase> reactionServices;

    public MovieRecommendationService(MovieRecommendationRepository movieRecommendationRepository, List<ReactionUseCase> reactionServices) {
        this.movieRecommendationRepository = movieRecommendationRepository;
        // map services to reactions to easily select service based on a reaction
        this.reactionServices = reactionServices.stream()
                                                .collect(toMap(ReactionUseCase::getReaction, service -> service));
    }

    @Transactional(readOnly = true)
    public Page<MovieRecommendationRepository.MovieWithReaction> fetchAllMovies(String usernameOfCurrentUser, Pageable pageable) {
        return movieRecommendationRepository.findAll(usernameOfCurrentUser, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieRecommendationRepository.MovieWithReaction> fetchAllMoviesOfUser(String usernameOfCurrentUser, String userName, Pageable pageable) {
        return movieRecommendationRepository.findAllPublishedBy(usernameOfCurrentUser, userName, pageable);
    }

    public MovieRecommendation save(MovieRecommendation movieRecommendation) {
        return Try.of(() -> movieRecommendationRepository.save(movieRecommendation))
                .getOrElseThrow(() -> new EntityExistsException("The Movie already exists"));
    }

    public void react(UUID movieId, User user, Reaction reaction) {
        var movieRecommendation =
                movieRecommendationRepository.findById(movieId)
                                             .orElseThrow(() -> new EntityNotFoundException("Movie not found."))
                                             .validate(user);

        reactionServices.get(reaction).react(movieRecommendation, user);
    }
}
