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
class LikeReactionService implements ReactionUseCase {

    private final ReactionRepository reactionRepository;

    @Override
    public Reaction getReaction() {
        return Reaction.LIKE;
    }

    @Override
    public void react(MovieRecommendation movieRecommendation, User user) {
        if (movieRecommendation.userAlreadyLikedTheMovie(user)) {
            throw new MultipleReactionsException("Cannot like a movie more than once.");
        } else if (movieRecommendation.userAlreadyHatedTheMovie(user)) {
            movieRecommendation.getUsersThatHated().remove(user);
            reactionRepository.decrementHates(movieRecommendation.getId());
        }
        movieRecommendation.getUsersThatLiked().add(user);
        reactionRepository.incrementLikes(movieRecommendation.getId());
    }
}
