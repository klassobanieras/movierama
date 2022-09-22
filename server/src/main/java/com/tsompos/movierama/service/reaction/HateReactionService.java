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
public class HateReactionService implements ReactionUseCase {

    private final MovieRepository movieRepository;

    @Override
    public Reaction getReaction() {
        return Reaction.HATE;
    }

    @Override
    public Mono<Void> react(MovieRecommendation movie, User user) {
        movie.validate(user.getId());
        if (movie.userAlreadyHatedTheMovie()) {
            throw new MultipleReactionsException("Cannot hate a movie more than once.");
        } else if (movie.userAlreadyLikedTheMovie()) {
            return movieRepository.decAndPullUserThatLikes(movie, user);
        }
        return movieRepository.incAndPushUserThatHates(movie, user);
    }


}
