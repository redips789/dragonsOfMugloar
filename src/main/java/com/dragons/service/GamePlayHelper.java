package com.dragons.service;

import com.dragons.model.Item;

import java.util.List;

public class GamePlayHelper {

    public static List<Item> getAffordableItems(List<Item> items, Integer availableGold) {
        return items.stream()
                .filter(item -> item.cost() < availableGold)
                .toList();
    }
}
