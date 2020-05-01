package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.ActionRequest;
import org.jgbakke.dominion.actions.ActionResponse;
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

    public void playCard(Player p, DominionCard card){
        // First get the +1 card, +2 actions etc.
        p.combineResources(card.turnBonusResources());

        // Then do the special ability and add any bonus resources
        ActionResponse executedBonusResources = (ActionResponse)
                card.executeAction(
                        new ActionRequest(this , p, new ModifierWrapper(0,0,0,0)));

        // Then combine the resources from both sources
        p.combineResources(executedBonusResources.resources);
    }

    private void takePlayerTurn(Player p){
        p.resetResources();

        while(p.getResources().actions > 0){
            // They played an action so reduce it by 1
            p.combineResources(new ModifierWrapper(-1, 0, 0, 0));

            // Pick a card
            DominionCard chosenCard = p.chooseAction(p.getResources().actions);

            if(chosenCard == null){
                break;
            }

            playCard(p, chosenCard);

        }

        // Now lay our coins on the table so everybody can see
        // "KEITH SHOW YOUR CARDS!!!"
        p.addTreasureToModifiers();

        List<DominionCard> chosenCards = p.buyPhase(p.getResources().coins);
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

        return players;
    }


}
