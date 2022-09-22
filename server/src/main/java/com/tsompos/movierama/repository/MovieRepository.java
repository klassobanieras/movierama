package com.tsompos.movierama.repository;

import com.tsompos.movierama.model.MovieReaction;
import com.tsompos.movierama.model.MovieRecommendation;
import com.tsompos.movierama.model.Reaction;
import com.tsompos.movierama.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static com.tsompos.movierama.model.MovieRecommendation.HATES_FIELD;
import static com.tsompos.movierama.model.MovieRecommendation.LIKES_FIELD;
import static com.tsompos.movierama.model.User.MOVIE_REACTIONS_FIELD;


@Repository
@RequiredArgsConstructor
@Transactional
public class MovieRepository {

    private final ReactiveMongoOperations reactiveOps;

    public Mono<Void> incAndPushUserThatHates(MovieRecommendation movie, User user) {
        var movieQuery = new Query().addCriteria(Criteria.where("_id").is(movie.getId()));
        var movieCommand = new Update().inc(HATES_FIELD, 1L);
        var userQuery = new Query().addCriteria(Criteria.where("_id")
                                                        .is(user.getId()));
        var userCommand = new Update().push(MOVIE_REACTIONS_FIELD, MovieReaction.builder()
                                                                                .reaction(Reaction.HATE)
                                                                                .movieId(movie.getId())
                                                                                .build());
        return reactiveOps.updateFirst(movieQuery, movieCommand, MovieRecommendation.class)
                          .then(reactiveOps.updateFirst(userQuery, userCommand, User.class))
                          .then();

    }

    public Mono<Void> decAndPullUserThatLikes(MovieRecommendation movie, User user) {
        var movieQuery = new Query().addCriteria(Criteria.where("_id").is(movie.getId()));
        var movieCommand = new Update().inc(LIKES_FIELD, -1L);
        var userQuery = new Query().addCriteria(Criteria.where("_id")
                                                        .is(user.getId()));
        var userCommand = new Update().pull(MOVIE_REACTIONS_FIELD, MovieReaction.builder()
                                                                                .reaction(Reaction.LIKE)
                                                                                .movieId(movie.getId())
                                                                                .build());
        return reactiveOps.updateFirst(movieQuery, movieCommand, MovieRecommendation.class)
                          .then(reactiveOps.updateFirst(userQuery, userCommand, User.class))
                          .then();
    }

    public Mono<Void> incAndPushUserThatLikes(MovieRecommendation movie, User user) {
        var movieQuery = new Query().addCriteria(Criteria.where("_id").is(movie.getId()));
        var movieCommand = new Update().inc(LIKES_FIELD, 1L);
        var userQuery = new Query().addCriteria(Criteria.where("_id")
                                                        .is(user.getId()));
        var userCommand = new Update().push(MOVIE_REACTIONS_FIELD, MovieReaction.builder()
                                                                                .reaction(Reaction.LIKE)
                                                                                .movieId(movie.getId())
                                                                                .build());
        return reactiveOps.updateFirst(movieQuery, movieCommand, MovieRecommendation.class)
                          .zipWith(reactiveOps.updateFirst(userQuery, userCommand, User.class))
                          .then();
    }

    public Mono<Void> decAndPullUserThatHates(MovieRecommendation movie, User user) {
        var movieQuery = new Query().addCriteria(Criteria.where("_id").is(movie.getId()));
        var movieCommand = new Update().inc(HATES_FIELD, -1L);
        var userQuery = new Query().addCriteria(Criteria.where("_id")
                                                        .is(user.getId()));
        var userCommand = new Update().pull(MOVIE_REACTIONS_FIELD, MovieReaction.builder()
                                                                                .reaction(Reaction.HATE)
                                                                                .movieId(movie.getId())
                                                                                .build());
        return reactiveOps.updateFirst(movieQuery, movieCommand, MovieRecommendation.class)
                          .zipWith(reactiveOps.updateFirst(userQuery, userCommand, User.class))
                          .then();
    }
}
