package com.tsompos.movierama.service;

import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import com.tsompos.movierama.repository.UserRepository;
import com.tsompos.movierama.service.reaction.ReactionUseCase;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Service
@Log4j2
public class MovieRecommendationService {

    private final MovieRecommendationRepository movieRecommendationRepository;
    private final UserRepository userRepository;
    private final Map<Reaction, ReactionUseCase> reactionServices;

    public MovieRecommendationService(MovieRecommendationRepository movieRecommendationRepository, UserRepository userRepository, List<ReactionUseCase> reactionServices) {
        this.movieRecommendationRepository = movieRecommendationRepository;
        this.userRepository = userRepository;
        // map services to reactions to easily select service based on a reaction
        this.reactionServices = reactionServices.stream()
                                                .collect(toMap(ReactionUseCase::getReaction, service -> service));
    }

    public Flux<MovieRecommendation> fetchAllMovies(String currentUsersName, Pageable pageable) {
        return userRepository.findByUserName(currentUsersName)
                             .flatMapMany(user -> movieRecommendationRepository.findAll(pageable.getSort())
                                                                               .map(movie -> movie.setCurrentUserReaction(user)));
    }

    public Flux<MovieRecommendation> fetchAllMoviesOfUser(String currentUsersName, String searchUsername, Pageable pageable) {
        return userRepository.findByUserName(currentUsersName).zipWith(userRepository.findByUserName(searchUsername))
                             .flatMapMany(zip -> movieRecommendationRepository.findByPublisherId(zip.getT2()
                                                                                                    .getId(), pageable.getSort())
                                                                              .map(movie -> movie.setCurrentUserReaction(zip.getT1())));
    }

    public Mono<MovieRecommendation> save(MovieRecommendation movieRecommendation, String currentUsersName) {
        return userRepository.findByUserName(currentUsersName)
                             .flatMap(user -> movieRecommendationRepository.save(MovieRecommendation.builder()
                                                                                                    .title(movieRecommendation.getTitle())
                                                                                                    .description(movieRecommendation.getDescription())
                                                                                                    .publisherId(user.getId())
                                                                                                    .build()));
    }

    public Mono<Void> react(UUID movieId, String currentUsersName, Reaction reaction) {
        return userRepository.findByUserName(currentUsersName)
                             .flatMap(user -> movieRecommendationRepository.findById(movieId)
                                                                           .map(movie -> movie.setCurrentUserReaction(user))
                                                                           .doOnNext(movieRecommendation -> log.info("{} is reacting {} on {}", currentUsersName, reaction.name(), movieRecommendation.getTitle()))
                                                                           .flatMap(movie -> reactionServices.get(reaction)
                                                                                                             .react(movie, user)));
    }
}
