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
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reaction reaction;

    @Builder
    private UserReaction(String email, Reaction reaction) {
        this.email = email;
        this.reaction = reaction;
    }
}
