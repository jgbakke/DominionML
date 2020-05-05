package org.jgbakke.dominion.players;

import org.jgbakke.dominion.DominionReward;
import org.jgbakke.dominion.DominionStateUpdater;
import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.*;
import org.jgbakke.jlearning.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class QLearningPlayer extends Player {
    private static final int THRONE_ROOM_ID = new ThroneRoom().id();
    private static final int CELLAR_ID = new Cellar().id();
    private QLearning qLearning;

    protected State currentState = new State(new DominionStateUpdater());

    public QLearningPlayer(Game game){
        super(game);
        this.name = "Dominionator";
        qLearning = new QLearning(new DominionReward(game));
    }

    @Override
    public DominionCard chooseAction(ModifierWrapper currentResources) {
        List<DominionCard> playableCards = hand.stream()
                .filter(c -> c.getCardType().equals(DominionCard.CardType.ACTION))
                .sorted(Comparator.comparingInt(c -> c.turnBonusResources().cards + c.turnBonusResources().actions))
                .collect(Collectors.toList());

        if(playableCards.isEmpty()){
            return null;
        }

        List<DominionCard> actionsOnly = new LinkedList<>();
        List<DominionCard> cardsOnly = new LinkedList<>();
        List<DominionCard> actionsAndCard = new LinkedList<>();

        DominionCard throneRoom = null;
        DominionCard cellar = null;
        int numPlayableCards = playableCards.size();

        for (DominionCard card : playableCards) {
            ModifierWrapper cardResources = card.turnBonusResources();

            if(card instanceof ThroneRoom){
                throneRoom = card;
            } else if (card instanceof Cellar){
                cellar = card;
            } else if (cardResources.cards > 0 && cardResources.actions > 0){
                actionsAndCard.add(card);
            } else {
                if (cardResources.actions > 0){
                    actionsOnly.add(card);
                } else if (cardResources.cards > 0){
                    cardsOnly.add(card);
                }
            }
        }

        if(currentResources.actions == 0){
            // Use a cellar to get another action and get rid of useless cards
            if(cellar != null){
                return cellar;
            }

            // Check that we have a throne room AND a card to play it on.
            // It's not much use if we don't have a card to use it on
            if(throneRoom != null && numPlayableCards > 1){
                return throneRoom;
            }

            // If we are out of actions, let's play something to get us more actions
            if(!actionsAndCard.isEmpty()){
                return actionsAndCard.get(0);
            }

            if(!actionsOnly.isEmpty()){
                return actionsOnly.get(0);
            }

        } else {
            // We have actions so let's get more cards instead
            if(!cardsOnly.isEmpty()){
                return cardsOnly.get(0);
            }

            if(!actionsAndCard.isEmpty()){
                return actionsAndCard.get(0);
            }
        }

        if(throneRoom != null && numPlayableCards > 1){
            return throneRoom;
        }

        // If we got here than either we have actions AND no +card cards
        // or we have no actions and NO +action actions
        // so let's see which options gets us more money
        double expectedCoinValue = averageCoinValue(false);
        DominionCard maxNumCards = cardsOnly.isEmpty() ? null : cardsOnly.get(0);

        DominionCard maxCoinValue = playableCards.stream()
                .max(Comparator.comparingInt(c -> c.turnBonusResources().coins))
                .orElse(null);

        // If we found an action card that gives us more money OR maxNumCards is null
        if(maxCoinValue != null &&
                (maxCoinValue.turnBonusResources().coins > expectedCoinValue ||
                        maxNumCards == null)
        ){
            return maxCoinValue;
        } else if (maxNumCards != null){
            // Else draw as many as we can
            return maxNumCards;
        }

        // If we got here than just return the first card
        return playableCards.get(0);

    }

    @Override
    public List<DominionCard> buyPhase(int coins, int buys) {
        LinkedList<DominionCard> chosenBuys = new LinkedList<>();

        while(coins > 1 && buys > 0){
            List<Action> validCards = validBuyChoices(coins);

            DominionCard chosen = null;

            if(coins >= 8){
                chosen = new Province();
            } else {
                Action qLearningAction = qLearning.chooseAction(currentState, validCards);

                if(qLearningAction != null){
                    chosen = (DominionCard)qLearningAction;
                }
            }

            if(chosen != null) {
                chosenBuys.add(chosen);
                coins -= chosen.cost();
            }

            buys--;

        }

        return chosenBuys;

    }

    @Override
    public void cleanup() {
        qLearning.saveToDB();
        //saveGameResult(name, getVictoryPoints());
    }

    private boolean canBuyCard(DominionCard card, int coins){
        return game.bank.hasCardsRemaining(card.id()) && card.cost() <= coins;
    }

    private boolean cardIsTooCheap(DominionCard card, int coins){
        //return !card.getCardType().equals(DominionCard.CardType.TREASURE);
        return card.cost() < coins - 1;
    }

    private List<Action> validBuyChoices(int coins){
        List<Action> validChoices = new LinkedList<>();

        for (Action action : ActionContainer.getInstance().getActions()) {
            DominionCard card = (DominionCard)action;

            if(canBuyCard(card, coins) && !cardIsTooCheap(card, coins)){
                validChoices.add(card);
            }
        }

        return validChoices;
    }

    @Override
    public void gainNewCard(DominionCard card){
        // Do not reward for cards given at start of game
        if(allCards.size() >= 10 && card.id() >= 0) {
            qLearning.updateQTable(currentState, card);
            currentState = currentState.getResultingState(card);
        }

        super.gainNewCard(card);
    }
}
