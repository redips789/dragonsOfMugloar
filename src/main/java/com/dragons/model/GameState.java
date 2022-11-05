package com.dragons.model;

import java.util.List;

public class GameState {

    private final String gameId;
    private Integer currentLives;

    private Integer availableGold;
    private final List<Item> items;

    public GameState(String gameId, Integer availableGold, Integer currentLives, List<Item> items) {
        this.gameId = gameId;
        this.availableGold = availableGold;
        this.currentLives = currentLives;
        this.items = items;
    }

    public String getGameId() {
        return gameId;
    }

    public Integer getCurrentLives() {
        return currentLives;
    }

    public List<Item> getItems() {
        return items;
    }

    public Integer getAvailableGold() {
        return availableGold;
    }

    public void setCurrentLives(Integer currentLives) {
        this.currentLives = currentLives;
    }

    public void setAvailableGold(Integer availableGold) {
        this.availableGold = availableGold;
    }
}
