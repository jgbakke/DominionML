package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.jlearning.Action;
import org.jgbakke.jlearning.ActionContainer;

// This class will store the numbers of each card remaining that is available to buy
public class Bank {
    private static final int MAX_ACTION_CARDS = 4;
    private static final int MAX_TREASURE_CARDS = 11;
    private static final int MAX_VICTORY_CARDS = 8;

    int[] cardsRemaining;

    ActionContainer ac;

    public Bank(){
        initStartingCards();
    }

    public boolean hasCardsRemaining(int cardID){
        return cardsRemaining[cardID] > 0;
    }

    public void takeCard(int cardID){
        if(cardID >= 0) {
            cardsRemaining[cardID]--;
        }
    }

    private void depleteCard(int cardId){
        cardsRemaining[cardId] = 0;
    }

    public boolean depleteCard(String cardName) {
        for (Action action : ac.getActions()) {
            if (action.getClass().getName().toLowerCase().contains(cardName.toLowerCase())){
                depleteCard(action.id());
                return true;
            }
        }

        return false;
    }

    private int numberOfCardsForType(DominionCard.CardType type){
        // For 3 items, a switch is probably faster and more memory-efficient than a HashMap
        // https://stackoverflow.com/questions/27993819/hashmap-vs-switch-statement-performance

        switch (type){
            case ACTION:
                return MAX_ACTION_CARDS;
            case TREASURE:
                return MAX_TREASURE_CARDS;
            case VICTORY:
                return MAX_VICTORY_CARDS;
            default:
                return 0;
        }
    }

    public void initStartingCards(){
        ac = ActionContainer.getInstance();
        Action[] actions = ac.getActions();

        int[] startingCardsCount = new int[actions.length];

        for (int i = 0; i < actions.length; i++) {
            DominionCard card = (DominionCard)actions[i];
            startingCardsCount[i] = numberOfCardsForType(card.getCardType());
        }

        cardsRemaining = startingCardsCount;

    }
}
