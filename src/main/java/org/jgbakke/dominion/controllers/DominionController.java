package org.jgbakke.dominion.controllers;

import org.jgbakke.dominion.Game;

import java.util.HashMap;

public class DominionController {

    public static void main(String[] args){
        String testId = "127.0.0.1Joe";
        DominionController exampleServer = new DominionController();

        exampleServer.startGame(testId);
        exampleServer.takeAITurn(testId);
        exampleServer.takeAITurn(testId);
        exampleServer.takeAITurn(testId);
        exampleServer.takeAITurn(testId);
        exampleServer.takeAITurn(testId);
        System.out.println(exampleServer.endGame(testId));
    }

    private HashMap<String, Game> gamesPlayed = new HashMap<>();

    public String startGame(String id){
        Game newGame = new Game(1 + gamesPlayed.size(), 1, 19);
        newGame.initPlayers();
        gamesPlayed.put(id, newGame);
        return "Game started! Press the button when it is the AI's turn.";
    }

    public String exhaustFromBank(String id, String card){
        Game game = gamesPlayed.get(id);

        if(game.bank.depleteCard(card.replace(" ", ""))){
            return "Affirmative.";
        } else {
            return "There is no card named " + card;
        }
    }

    public String takeAITurn(String id){
        Game game = gamesPlayed.get(id);
        return game.takeAiPlayerTurn();
    }

    public String endGame(String id){
        String retval = gamesPlayed.get(id).getVictoryPoints(0) + " Victory Points";
        gamesPlayed.remove(id);
        return retval;
    }
}
