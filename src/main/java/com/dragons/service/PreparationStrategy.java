package com.dragons.service;

import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;

import java.util.List;

public interface PreparationStrategy {

    boolean valid(Integer lives, MessageWithCategory messageWithCategory, String gameId, List<Item> affordableItems);

    void apply(String gameId, List<Item> affordableItems);
}
