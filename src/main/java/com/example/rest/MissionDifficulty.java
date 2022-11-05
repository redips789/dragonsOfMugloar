package com.example.rest;

import java.util.Arrays;

public enum MissionDifficulty {
    EASY(new String[]{"Walk in the park", "Sure thing", "Piece of cake", "Quite likely"}),
    MEDIUM(new String[]{"Hmmm....", "Risky", "Gamble", "Rather detrimental"}),
    HARD(new String[]{}),
    OTHER(new String[0]);

    private final String[] values;

    MissionDifficulty(String[] values) {
        this.values = values;
    }

    public static MissionDifficulty getCategory(String probability) {
        for (MissionDifficulty difficulty : MissionDifficulty.values()) {
            if (Arrays.asList(difficulty.values).contains(probability)) {
                return difficulty;
            }
        }

        return OTHER;
    }
}
