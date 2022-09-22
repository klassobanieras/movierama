package com.tsompos.movierama.service.reaction;

import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NoneReactionService implements ReactionUseCase {

    @Override
    public Reaction getReaction() {
        return Reaction.NONE;
    }

    @Override
    public Mono<Void> react(MovieRecommendation movie, User user) {
        return Mono.empty();
    }
}
