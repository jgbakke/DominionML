package org.jgbakke.dominion.players;

import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.actions.ThroneRoom;
import org.jgbakke.dominion.actions.Woodcutter;
import org.jgbakke.dominion.treasures.Copper;
import org.jgbakke.dominion.treasures.Gold;
import org.jgbakke.dominion.treasures.Silver;
import org.jgbakke.dominion.treasures.Treasure;

import java.util.*;
import java.util.stream.Stream;

public abstract class Player {
    private static final int HAND_SIZE = 5;

    private ModifierWrapper resources = new ModifierWrapper(0,0,0,0);

    protected String name;
    protected Stack<DominionCard> deck = new Stack<>();
    protected Stack<DominionCard> discard = new Stack<>();
    protected ArrayList<DominionCard> hand = new ArrayList<>();

    public void setStartingDeck(){
        for(int i = 0; i < 7; i++){
            discard.add(new Copper());
        }

        for(int i = 7; i < 9; i++){
            discard.add(new Silver());
        }

        discard.add(new Gold());

        shuffleDeck();
    }

    public ModifierWrapper getResources(){
        return resources;
    }

    public void resetResources(){
        resources = new ModifierWrapper(1,0,0,1);
    }

    public void combineResources(ModifierWrapper combined){
        resources.combineWith(combined);
    }

    public void drawHand(){
        for (int i = 0; i < HAND_SIZE; i++) {
            addCardToHand();
        }
    }

    protected void shuffleDeck(){
        Collections.shuffle(discard);
        deck = discard;
        discard = new Stack<>();
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

    public void addTreasureToModifiers(){
        resources.combineWith(new ModifierWrapper(0, 0, treasureValueInHand(), 0));
    }

    public int treasureValueInHand(){
        return hand.stream()
                .filter(c -> c.getCardType().equals(DominionCard.CardType.TREASURE))
                .map(c -> (Treasure)c)
                .mapToInt(c -> c.VALUE)
                .sum();
    }

    public void gainNewCard(DominionCard card){
        discard.add(card);
    }

    /// Return the card you want to play
    public abstract DominionCard chooseAction(int actionsRemaining);

    /// Return a List of the cards you are buying this turn
    public abstract List<DominionCard> buyPhase(int coins);

}
