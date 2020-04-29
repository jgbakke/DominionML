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
    }

    public Game(int id, int players, int maxRounds){
        this.id = id;
        this.players = createPlayers(players);
        this.maxRounds = maxRounds;
    }

    private void takeTurn(){
        for(int round = 0; round < maxRounds; round++){
            for (Player player : players) {
                takePlayerTurn(player);
            }
        }
    }

    private void takePlayerTurn(Player p){
        ModifierWrapper currentResources = new ModifierWrapper(1,0,1,0);

        while(currentResources.actions > 0){
            DominionCard chosenCard = p.chooseAction(currentResources.actions);

            // TODO: Implement getting bonus resources
            if(chosenCard == null){
                // They did not play one so let's move on
                break;
            }
        }

        List<DominionCard> chosenCards = p.buyPhase(currentResources.coins);
        chosenCards.forEach(p::gainNewCard);

        p.discardHand();
        p.drawHand();
    }

    private Player[] createPlayers(int num){
        players = new Player[num];

        if(num == 1){
            players[0] = new QLearningPlayer();
        } else {
            throw new NotImplementedException();
        }

        for (Player player : players) {
            player.setStartingDeck();
        }

        return players;
    }


}
