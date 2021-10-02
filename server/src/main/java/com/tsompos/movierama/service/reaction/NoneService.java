package com.tsompos.movierama.service.reaction;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;
import com.tsompos.movierama.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class NoneService implements ReactionService {

    private final ReactionRepository reactionRepository;

    @Override
    public Reaction getReaction() {
        return Reaction.NONE;
    }

    @Override
    public void react(MovieRecommendation movieRecommendation, User user) {
        if (movieRecommendation.userAlreadyHatedTheMovie(user)) {
            movieRecommendation.getUsersThatHated().remove(user);
            reactionRepository.decrementHates(movieRecommendation.getId());
        } else if (movieRecommendation.userAlreadyLikedTheMovie(user)) {
            movieRecommendation.getUsersThatLiked().remove(user);
            reactionRepository.decrementLikes(movieRecommendation.getId());
        }
    }
}
