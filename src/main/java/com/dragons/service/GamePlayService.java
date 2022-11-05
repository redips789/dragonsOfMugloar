package com.dragons.service;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.model.Item;
import com.dragons.model.Message;
import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        var gameState = new GameState(startGameResponse.gameId(), startGameResponse.lives(), new ArrayList<>());
        var currentScore = startGameResponse.score();
        var availableGold = startGameResponse.gold();

        while (gameState.getCurrentLives() > 0) {
            var messageWithCategory = getMessageWithCategory(gameState.getGameId());
            gameState.getAffordableItems().addAll(getAffordableItems(gameState.getGameId(), availableGold));
            missionPreparatory.prepare(gameState, messageWithCategory, preparationStrategies);
            var solveMessageResponse = dragonsOfMugloarApi.solveMessage(gameState.getGameId(), messageWithCategory.adId());
            gameState.setCurrentLives(solveMessageResponse.lives());
            currentScore = solveMessageResponse.score();
            availableGold = solveMessageResponse.gold();

            if (gameState.getCurrentLives() == 0) {
                break;
            }
            gameState.getAffordableItems().clear();
        }

        return currentScore;
    }

    private MessageWithCategory getMessageWithCategory(String gameId) {
        var messages = dragonsOfMugloarApi.getMessages(gameId);
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

    private List<Item> getAffordableItems(String gameId, Integer availableGold) {
        var items = dragonsOfMugloarApi.listItemsAvailableInShop(gameId);
        return items.stream()
                .filter(item -> item.cost() < availableGold)
                .toList();
    }
}
