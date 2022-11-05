package com.dragons.model;

public record StartGameResponse(String gameId, Integer lives, Integer gold, Integer level, Integer score,
                                Integer highScore, Integer turn) {
}
