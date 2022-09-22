package com.tsompos.movierama.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode
@Document
@ToString
public class MovieReaction {

    private UUID movieId;
    private Reaction reaction;

}
