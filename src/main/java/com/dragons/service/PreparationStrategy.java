package com.dragons.service;

import com.dragons.model.GameState;

public interface PreparationStrategy extends Comparable<PreparationStrategy> {

    boolean valid(GameState gameState);

    void apply(GameState gameState);

    Integer getPriority();

}
