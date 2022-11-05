package com.dragons.service;

import com.dragons.model.MessageWithCategory;

public interface PreparationStrategy {

    boolean valid(Integer lives, MessageWithCategory messageWithCategory, String gameId, Integer availableGold);

    void apply(String gameId);
}
