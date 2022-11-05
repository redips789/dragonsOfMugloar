package com.dragons.model;

import java.util.Arrays;

public enum MissionDifficulty {
    EASY(new String[]{"Walk in the park", "Sure thing", "Piece of cake"}),
    MEDIUM(new String[]{"Hmmm....", "Risky", "Gamble", "Rather detrimental", "Playing with fire", "Quite likely"}),
    OTHER(new String[0]),
    HARD(new String[]{"Suicide mission", "Impossible"});

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
