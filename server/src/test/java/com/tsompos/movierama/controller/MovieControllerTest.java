package com.tsompos.movierama.controller;

import com.tsompos.movierama.service.MovieRecommendationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import static com.tsompos.movierama.config.ApplicationConfiguration.MOVIES_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("username", "1234567").build();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MovieRecommendationService movieRecommendationService;

    @Test
    @SneakyThrows
    void shouldFetchMoviesSuccessfully() {
        //given
        Mockito.when(movieRecommendationService.fetchAllMovies(any())).thenReturn(Page.empty());
        //when
        mockMvc.perform(get(MOVIES_URL)).andExpect(status().isOk());
    }


    @Test
    @SneakyThrows
    void shouldReturnConflictWhenSameMovieExists() {
        //given
        Mockito.when(movieRecommendationService.save(any(), any())).thenThrow(new EntityExistsException());
        //when
        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwt))).andExpect(status().isConflict());
    }

    @Test
    @SneakyThrows
    void shouldThrowInternalServerErrorWhenGenericExceptionIsThrown() {
        //given
        Mockito.when(movieRecommendationService.save(any(), any())).thenThrow(new RuntimeException("null"));
        //when
        mockMvc.perform(post(MOVIES_URL).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\": \"a Title\", \"description\": \"a " + "Description\"}")
            .with(jwt().jwt(jwt))).andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    void shouldThrowEntityNotFoundExceptionWhenTheMovieDoesNotExistWhenReactingToAMovie() {
        //given
        Mockito.doThrow(new EntityNotFoundException()).when(movieRecommendationService).addReaction(any(), any(), any());
        //when
        mockMvc.perform(
            put(MOVIES_URL + "/{movieId}" + "/like", 123).contentType(MediaType.APPLICATION_JSON).with(jwt().jwt(jwt)))
            .andExpect(status().isNotFound());
    }
}