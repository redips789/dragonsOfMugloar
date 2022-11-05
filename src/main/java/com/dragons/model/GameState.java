package com.dragons.model;

import java.util.List;

public class GameState {

    private final String gameId;
    private Integer currentLives;
    private final List<Item> affordableItems;

    public GameState(String gameId, Integer currentLives, List<Item> affordableItems) {
        this.gameId = gameId;
        this.currentLives = currentLives;
        this.affordableItems = affordableItems;
    }

    public String getGameId() {
        return gameId;
    }

    public Integer getCurrentLives() {
        return currentLives;
    }

    public List<Item> getAffordableItems() {
        return affordableItems;
    }

    public void setCurrentLives(Integer currentLives) {
        this.currentLives = currentLives;
    }
}
