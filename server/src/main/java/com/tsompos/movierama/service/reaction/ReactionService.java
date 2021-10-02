package com.tsompos.movierama.service.reaction;

import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.entity.Reaction;
import com.tsompos.movierama.entity.User;

public interface ReactionService {

    Reaction getReaction();

    void react(MovieRecommendation movieId, User user);
}
