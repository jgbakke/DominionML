package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.ActionRequest;
import org.jgbakke.dominion.actions.ActionResponse;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.players.Player;
import org.jgbakke.dominion.players.QLearningPlayer;
import org.jgbakke.jlearning.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class Game {
    public final Bank bank;

    private int id;

    private int maxRounds;

    private Player[] players;

    public static void main(String[] args){
        Game g = new Game(-2, 1, 20);
        g.startGame();
    }

    public Game(int id, int players, int maxRounds){
        this.id = id;
        this.players = createPlayers(players);
        this.maxRounds = maxRounds;
        this.bank = new Bank();
    }

    public void startGame(){
        long start = System.nanoTime();

        for (Player player : players) {
            player.setStartingDeck();
            player.drawHand();
        }

        takeTurns();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        double ms = elapsed / Math.pow(10, 6);
        Logger.log(id, String.format("GAME ENDED IN %.2f MS", ms), Logger.LoggingSeverity.WARN);

    }

    private void takeTurns(){
        for(int round = 0; round < maxRounds; round++){
            for (Player player : players) {
                takePlayerTurn(player);
            }
        }
    }

    public void playCard(Player p, DominionCard card){
        // We played it so remove it from the hand
        p.discardSpecificCard(card);

        // First get the +1 card, +2 actions etc.
        p.combineResources(card.turnBonusResources());

        // Then draw your new cards and mark that you did so
        useCardBonus(p);

        // Then do the special ability and add any bonus resources
        ActionResponse executedBonusResources = (ActionResponse)
                card.executeAction(
                        new ActionRequest(this , p, new ModifierWrapper(0,0,0,0)));

        // Then combine the resources from both sources
        p.combineResources(executedBonusResources.resources);

        // We may have accumulated new cards from the action. Redeem them here
        // Note that THIS IS IMPORTANT to do in both places. +x Cards MUST be done BEFORE the card's action
        useCardBonus(p);
    }

    private void useCardBonus(Player p){
        p.drawCards(p.getResources().cards);
        p.getResources().resetCards();
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

        List<DominionCard> chosenCards = p.buyPhase(p.getResources().coins, p.getResources().buys);
        giveCardsToPlayer(chosenCards, p);

        p.discardHand();
        p.drawHand();
    }

    private void giveCardsToPlayer(List<DominionCard> cards, Player p){
        cards.forEach( c -> {
            bank.takeCard(c.id());
            p.gainNewCard(c);
        });
    }

    private Player[] createPlayers(int num){
        players = new Player[num];

        if(num == 1){
            players[0] = new QLearningPlayer(this);
        } else {
            throw new NotImplementedException();
        }

        return players;
    }


}
