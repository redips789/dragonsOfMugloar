package com.dragons.service;

import com.dragons.model.GameState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionPreparatory {

    public void prepare(GameState gameState, List<PreparationStrategy> strategyList) {
        strategyList.stream()
                .filter(strategy -> strategy.valid(gameState))
                .forEach(strategy -> strategy.apply(gameState));
    }
}
