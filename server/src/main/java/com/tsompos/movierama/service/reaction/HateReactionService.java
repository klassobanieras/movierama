package com.tsompos.movierama.service.reaction;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;
import com.tsompos.movierama.error.MultipleReactionsException;
import com.tsompos.movierama.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HateReactionService implements ReactionUseCase {

    private final ReactionRepository reactionRepository;

    @Override
    public Reaction getReaction() {
        return Reaction.HATE;
    }

    @Override
    public void react(MovieRecommendation movieRecommendation, User user) {
        if (movieRecommendation.userAlreadyHatedTheMovie(user)) {
            throw new MultipleReactionsException("Cannot hate a movie more than once.");
        } else if (movieRecommendation.userAlreadyLikedTheMovie(user)) {
            movieRecommendation.getUsersThatLiked().remove(user);
            reactionRepository.decrementLikes(movieRecommendation.getId());
        }
        movieRecommendation.getUsersThatHated().add(user);
        reactionRepository.incrementHates(movieRecommendation.getId());
    }
}
