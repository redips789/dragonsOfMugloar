package com.dragons.service;

import com.dragons.model.GameState;
import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionPreparatory {

    public void prepare(GameState gameState, MessageWithCategory messageWithCategory,
                        List<PreparationStrategy> strategyList) {
        strategyList.stream()
                .filter(strategy -> strategy.valid(gameState, messageWithCategory))
                .forEach(strategy -> strategy.apply(gameState));
    }
}
