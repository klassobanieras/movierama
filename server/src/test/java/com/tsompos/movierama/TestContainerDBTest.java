package com.tsompos.movierama;

import com.tsompos.movierama.repository.MovieRecommendationRepository;
import com.tsompos.movierama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Testcontainers
public abstract class TestContainerDBTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.9");
    @Autowired
    protected MovieRecommendationRepository movieRecommendationRepository;
    @Autowired
    protected UserRepository userRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    protected Mono<Tuple2<Void, Void>> cleanDB() {
        return Mono.zip(userRepository.deleteAll(), movieRecommendationRepository.deleteAll());
    }
}
