package com.tsompos.movierama.model;

public enum Reaction {
    LIKE, HATE, NONE;

    public static Reaction fromInput(String input) {
        return valueOf(input.toUpperCase());
    }
}
