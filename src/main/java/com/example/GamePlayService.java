package com.example;

import com.example.client.DragonsOfMugloarApi;
import com.example.rest.Message;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GamePlayService {

    private List<String> easy = List.of("Walk in the park", "Sure thing", "Piece of cake", "Quite likely");

    private Set<String> distinctProbability = new HashSet<>();

    private DragonsOfMugloarApi dragonsOfMugloarApi;

    public GamePlayService(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    public void play() {
        var gameState = dragonsOfMugloarApi.startGame();
        var currentScore = gameState.score();
        while (gameState.lives() > 0) {
            var messages = dragonsOfMugloarApi.getMessages(gameState.gameId());
            distinctProbability.addAll(messages.stream().map(Message::probability).collect(Collectors.toList()));
            var easyMessage = messages.stream().filter(t -> easy.contains(t.probability())).collect(Collectors.toList());
            Comparator<Message> messageComparator = Comparator.comparing(Message::reward).reversed();
            easyMessage.sort(messageComparator);
            if (easyMessage.size() > 0) {
                var solutionResponse = dragonsOfMugloarApi.solveMessage(gameState.gameId(), easyMessage.stream().findFirst().orElseThrow().adId());
                currentScore = solutionResponse.score();
                System.out.println(solutionResponse);

                if (solutionResponse.lives() == 0) {
                    System.out.println(distinctProbability);
                    break;
                }

                if (solutionResponse.lives() == 1) {
                    var items = dragonsOfMugloarApi.listItemsAvailableInShop(gameState.gameId());
                    var affordableItems = items.stream().filter(t -> t.cost() < solutionResponse.gold()).toList();
                    var availableHpPotions = items.stream().filter(t -> t.id().equals("hpot") && t.cost() < solutionResponse.gold()).toList();
                    if (availableHpPotions.size() > 0) {
                        dragonsOfMugloarApi.purchaseShopItem(gameState.gameId(), availableHpPotions.stream().findAny().orElseThrow().id());
                    } else if (affordableItems.size() > 0) {
                        dragonsOfMugloarApi.purchaseShopItem(gameState.gameId(), affordableItems.stream().findAny().orElseThrow().id());
                    }
                }

            } else {
                System.out.println("No easy message");
                break;
            }

        }
        System.out.println("Current score: " + currentScore);
    }

}
