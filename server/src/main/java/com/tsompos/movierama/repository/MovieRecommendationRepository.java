package com.tsompos.movierama.repository;

import com.tsompos.movierama.model.MovieRecommendation;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface MovieRecommendationRepository extends ReactiveMongoRepository<MovieRecommendation, UUID> {

    Flux<MovieRecommendation> findByPublisherId(UUID userId, Sort sort);
}
