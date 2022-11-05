package com.dragons.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameState {

    private final String gameId;
    private Integer currentLives;

    private Integer availableGold;
    private final List<Item> itemsAvailableToBuy = new ArrayList<>();
    private final Set<Item> currentItems = new HashSet<>();

    public GameState(String gameId, Integer availableGold, Integer currentLives, List<Item> items) {
        this.gameId = gameId;
        this.availableGold = availableGold;
        this.currentLives = currentLives;
        this.itemsAvailableToBuy.addAll(items);
    }

    public String getGameId() {
        return gameId;
    }

    public Integer getCurrentLives() {
        return currentLives;
    }

    public List<Item> getItemsAvailableToBuy() {
        return itemsAvailableToBuy;
    }

    public Set<Item> getCurrentItems() {
        return currentItems;
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
