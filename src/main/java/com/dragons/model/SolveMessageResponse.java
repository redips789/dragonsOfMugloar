package com.dragons.model;

public record SolveMessageResponse(Boolean success,
                                   Integer lives,
                                   Integer gold,
                                   Integer score,
                                   Integer highScore,
                                   Integer turn,
                                   String message) {
}
