package com.tsompos.movierama.controller;

import com.tsompos.movierama.service.MovieRecommendationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import static com.tsompos.movierama.config.ApplicationConfiguration.MOVIES_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class MovieControllerTest {

    private final Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("email", "1234567").build();
    private MockMvc mockMvc;
    @MockBean
    private MovieRecommendationService movieRecommendationService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    @SneakyThrows
    void shouldFetchMoviesSuccessfully() {
        //given
        Mockito.when(movieRecommendationService.fetchAllMovies(any(), any())).thenReturn(Page.empty());
        //when
        mockMvc.perform(get(MOVIES_URL)).andExpect(status().isOk()).andDo(document("movies"));
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