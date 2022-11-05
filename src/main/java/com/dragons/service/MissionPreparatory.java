package com.dragons.service;

import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionPreparatory {

    public void prepare(Integer lives, MessageWithCategory messageWithCategory, String gameId, Integer availableGold,
                        List<PreparationStrategy> strategyList) {
        strategyList.stream()
                .filter(strategy -> strategy.valid(lives, messageWithCategory, gameId, availableGold))
                .forEach(strategy -> strategy.apply(gameId));
    }
}
