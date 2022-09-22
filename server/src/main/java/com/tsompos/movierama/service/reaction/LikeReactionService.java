package com.tsompos.movierama.service.reaction;

import com.tsompos.movierama.error.MultipleReactionsException;
import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.model.User;
import com.tsompos.movierama.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LikeReactionService implements ReactionUseCase {

    private final MovieRepository repository;

    @Override
    public Reaction getReaction() {
        return Reaction.LIKE;
    }

    @Override
    public Mono<Void> react(MovieRecommendation movie, User user) {
        movie.validate(user.getId());
        if (movie.userAlreadyLikedTheMovie()) {
            throw new MultipleReactionsException("Cannot like a movie more than once.");
        } else if (movie.userAlreadyHatedTheMovie()) {
            return repository.decAndPullUserThatHates(movie, user);
        }
        return repository.incAndPushUserThatLikes(movie, user);
    }
}
