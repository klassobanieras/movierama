package com.tsompos.movierama.service.reaction;

import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.model.User;
import reactor.core.publisher.Mono;

public interface ReactionUseCase {

    Reaction getReaction();

    Mono<Void> react(MovieRecommendation movie, User user);
}
