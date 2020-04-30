package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.players.Player;
import org.jgbakke.dominion.players.QLearningPlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class Game {
    private int id;

    private int maxRounds;

    private Player[] players;

    public static void main(String[] args){
        Game g = new Game(-2, 1, 5);
        g.startGame();
    }

    public Game(int id, int players, int maxRounds){
        this.id = id;
        this.players = createPlayers(players);
        this.maxRounds = maxRounds;
    }

    public void startGame(){
        for (Player player : players) {
            player.setStartingDeck();
            player.drawHand();
        }

        takeTurns();
    }

    private void takeTurns(){
        for(int round = 0; round < maxRounds; round++){
            for (Player player : players) {
                takePlayerTurn(player);
            }
        }
    }

    private void takePlayerTurn(Player p){
        System.out.println("Starting turn...");
        ModifierWrapper currentResources = new ModifierWrapper(1,0,1,0);

        while(currentResources.actions > 0){
            DominionCard chosenCard = p.chooseAction(currentResources.actions);

            if(chosenCard == null){
                break;
            }

            chosenCard.executeAction();
            currentResources.actions--;
        }

        List<DominionCard> chosenCards = p.buyPhase(currentResources.coins);
        chosenCards.forEach(p::gainNewCard);

        System.out.println("Ending turn...");
        p.discardHand();
        p.drawHand();
        System.out.println("Hand drawn");
    }

    private Player[] createPlayers(int num){
        players = new Player[num];

        if(num == 1){
            players[0] = new QLearningPlayer();
        } else {
            throw new NotImplementedException();
        }

        return players;
    }


}
