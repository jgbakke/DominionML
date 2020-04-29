package org.jgbakke.dominion.players;

import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.actions.ThroneRoom;
import org.jgbakke.dominion.actions.Woodcutter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public abstract class Player {
    private static final int HAND_SIZE = 5;

    protected String name;
    protected Stack<DominionCard> deck = new Stack<>();
    protected Stack<DominionCard> discard = new Stack<>();
    protected ArrayList<DominionCard> hand = new ArrayList<>();

    public void setStartingDeck(){
        for(int i = 0; i < 7; i++){
            discard.add(new ThroneRoom());
        }

        for(int i = 7; i < 10; i++){
            discard.add(new Woodcutter());
        }

        shuffleDeck();
    }

    public void drawHand(){
        for (int i = 0; i < HAND_SIZE; i++) {
            addCardToHand();
        }

        System.out.println("I drew my hand!");
    }

    protected void shuffleDeck(){
        Collections.shuffle(discard);
        deck = discard;
        discard = new Stack<>();
        System.out.println("It was shuffled.");
    }

    protected void addCardToHand(){
        if(deck.empty()){
            shuffleDeck();
        }

        hand.add(deck.pop());
    }

    public void discardHand(){
        discard.addAll(hand);
        hand.clear();
    }

    public void gainNewCard(DominionCard card){
        discard.add(card);
    }

    /// Return the card you want to play
    public abstract DominionCard chooseAction(int actionsRemaining);

    /// Return a List of the cards you are buying this turn
    public abstract List<DominionCard> buyPhase(int coins);

}
