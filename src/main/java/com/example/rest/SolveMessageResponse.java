package com.example.rest;

public record SolveMessageResponse(Boolean success, Integer lives, Integer gold, Integer score, Integer highScore,
                                   Integer turn, String message) {
}
