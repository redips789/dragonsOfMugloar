package com.dragons.service;

import com.dragons.model.GameState;
import com.dragons.model.MessageWithCategory;

public interface PreparationStrategy extends Comparable<PreparationStrategy> {

    boolean valid(GameState gameState, MessageWithCategory messageWithCategory);

    void apply(GameState gameState);

    Integer getPriority();
}
