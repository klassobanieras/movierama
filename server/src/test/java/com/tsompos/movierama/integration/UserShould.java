package com.tsompos.movierama.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsompos.movierama.entity.MovieRecommendation;
import com.tsompos.movierama.repository.MovieRecommendationRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static com.tsompos.movierama.config.ApplicationConfiguration.MOVIES_URL;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserShould {

    private final Jwt jwtOfMoviePublisher = Jwt.withTokenValue("token").header("alg", "none").claim("email", "1234567").build();
    private final Jwt jwtOfAnotherUser = Jwt.withTokenValue("token").header("alg", "none").claim("email", "12345678").build();
    private final Jwt jwtOfThirdUser = Jwt.withTokenValue("token").header("alg", "none").claim("email", "123456789").build();

    @Autowired
    MovieRecommendationRepository movieRecommendationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        movieRecommendationRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void beAbleToFetchMoviesUnauthorized() {
        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());

        mockMvc.perform(get(MOVIES_URL))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].currentUserReaction", is("NONE")));
    }

    @Test
    @SneakyThrows
    void beAbleToFetchMoviesAuthenticated() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());

        mockMvc.perform(get(MOVIES_URL).contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].currentUserReaction", is("LIKE")));

    }

    @Test
    @SneakyThrows
    void beAbleToFetchMoviesIsSortedByDefault() {

        //given
        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andReturn().getResponse().getContentAsString();

        mockMvc.perform(get(MOVIES_URL).contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.sort.sorted", is(true)));

    }

    @Test
    @SneakyThrows
    void beAbleToPostAMovieAuthenticated() {

        String response = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();


        var movieRecommendation = objectMapper.readValue(response, MovieRecommendation.class);

        assertEquals("1234567", movieRecommendation.getPublishedBy());
        assertNotNull(movieRecommendation.getPublishedDate());
    }

    @Test
    @SneakyThrows
    void beAbleToLikeAMovieRecommendation() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void beAbleToHateAMovieRecommendation() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/hate", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/hate", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfThirdUser))).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void notBeAbleToVoteAgainOnAHatedMovie() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/hate", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());

        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/hate", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isBadRequest());

        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void notBeAbleToVoteAgainOnALikedMovie() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());

        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/hate", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isBadRequest());

        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void notBeAbleToVoteOnHisOwnMovieRecommendations() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isBadRequest());

        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/hate", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldBeAbleToRemoveHisReaction() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());

        mockMvc.perform(delete(MOVIES_URL + "/{movieId}" + "/reaction", movieRecommendation.getMovieId()).contentType(
            MediaType.APPLICATION_JSON).with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldBeAbleToFetchMoviesOfAUser() {

        //given
        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Second Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a third Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a fourth Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfAnotherUser))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        //when
        mockMvc.perform(
            get(MOVIES_URL + "/{username}", jwtOfMoviePublisher.getClaims().get("username")).contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(jwtOfAnotherUser))).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void beAbleToReactToAMovieRecommendationConcurrently() {

        //given
        String postMovieResponse = mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwtOfMoviePublisher))).andReturn().getResponse().getContentAsString();

        var movieRecommendation = objectMapper.readValue(postMovieResponse, MovieRecommendation.class);

        //when
        var firstCall = CompletableFuture.supplyAsync(() -> like(movieRecommendation.getMovieId(), jwtOfAnotherUser));
        var secondCall = CompletableFuture.supplyAsync(() -> like(movieRecommendation.getMovieId(), jwtOfThirdUser));

        CompletableFuture.allOf(firstCall, secondCall).get();
    }

    @SneakyThrows
    private int like(Long movieId, Jwt jwt) {
        return mockMvc.perform(post(MOVIES_URL + "/{movieId}" + "/reaction/like", movieId).contentType(MediaType.APPLICATION_JSON)
            .with(jwt().jwt(jwt))).andExpect(status().isOk()).andReturn().getResponse().getStatus();
    }
}
