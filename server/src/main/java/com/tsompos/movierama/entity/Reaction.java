package com.tsompos.movierama.entity;

public enum Reaction {
    LIKE, HATE, NONE;

    public static Reaction fromInput(String input) {
        return valueOf(input.toUpperCase());
    }
}
