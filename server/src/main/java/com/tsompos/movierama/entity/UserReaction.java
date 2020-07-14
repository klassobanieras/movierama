package com.tsompos.movierama.entity;

import lombok.*;

import javax.persistence.*;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserReaction {

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String username;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reaction reaction;

    @Builder
    private UserReaction(String username, Reaction reaction) {
        this.username = username;
        this.reaction = reaction;
    }
}
