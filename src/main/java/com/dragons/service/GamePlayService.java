package com.dragons.service;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.model.Message;
import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GamePlayService {

    private final DragonsOfMugloarApi dragonsOfMugloarApi;
    private final MissionPreparatory missionPreparatory;
    private final List<PreparationStrategy> preparationStrategies;

    public GamePlayService(DragonsOfMugloarApi dragonsOfMugloarApi, MissionPreparatory missionPreparatory,
                           List<PreparationStrategy> preparationStrategies) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
        this.missionPreparatory = missionPreparatory;
        this.preparationStrategies = preparationStrategies.stream().sorted().collect(Collectors.toList());
    }

    public Integer play() {
        var startGameResponse = dragonsOfMugloarApi.startGame();
        var gameState = new GameState(startGameResponse.gameId(), startGameResponse.gold(), startGameResponse.lives(),
                dragonsOfMugloarApi.listAvailableItems(startGameResponse.gameId()));
        var currentScore = startGameResponse.score();

        while (gameState.getCurrentLives() > 0) {
            ;
            missionPreparatory.prepare(gameState, preparationStrategies);
            var messageWithCategory = getMessageWithCategory(gameState.getGameId());
            var solveMessageResponse = dragonsOfMugloarApi.solveMessage(gameState.getGameId(),
                    messageWithCategory.adId());

            gameState.setCurrentLives(solveMessageResponse.lives());
            gameState.setAvailableGold(solveMessageResponse.gold());
            currentScore = solveMessageResponse.score();

            if (gameState.getCurrentLives() == 0) {
                break;
            }
        }

        return currentScore;
    }

    private MessageWithCategory getMessageWithCategory(String gameId) {
        var messages = dragonsOfMugloarApi.getAllMessages(gameId);
        var messagesWithGroup = messages.stream()
                .map(Message::mapTo)
                .collect(Collectors.toList());

        Comparator<MessageWithCategory> messageComparator = Comparator.comparing(MessageWithCategory::difficulty)
                .thenComparing(Comparator.comparingInt(MessageWithCategory::reward)
                        .reversed());

        messagesWithGroup.sort(messageComparator);

        return messagesWithGroup.stream()
                .findFirst()
                .orElseThrow();
    }
}
