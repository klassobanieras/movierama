package com.tsompos.movierama.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.ProjectedPayload;

import java.time.LocalDateTime;

@ProjectedPayload
public interface MovieProjection {

    @Value("#{target.id}")
    Long getId();

    String getTitle();

    String getDescription();

    @Value("#{target.COUNT_OF_LIKES}")
    long getCountOfLikes();

    @Value("#{target.COUNT_OF_HATES}")
    long getCountOfHates();

    @Value("#{target.CURRENT_USER_REACTION == null ? 'NONE' : target.CURRENT_USER_REACTION}")
    String getCurrentUserReaction();

    @Value("#{target.PUBLISHED_BY}")
    String getPublishedBy();

    @Value("#{target.PUBLISHED_DATE}")
    LocalDateTime getPublishedDate();
}
