package com.tsompos.movierama.repository;

import com.tsompos.movierama.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {

    Mono<User> findByUserName(String username);
}
