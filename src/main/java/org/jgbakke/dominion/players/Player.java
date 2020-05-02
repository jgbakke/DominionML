package org.jgbakke.dominion.players;

import org.jgbakke.dominion.HandVisitor;
import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.*;
import org.jgbakke.dominion.treasures.Copper;
import org.jgbakke.dominion.treasures.Gold;
import org.jgbakke.dominion.treasures.Treasure;
import org.jgbakke.dominion.victories.Estate;
import org.jgbakke.dominion.victories.Victory;

import java.util.*;

public abstract class Player {
    private static final int HAND_SIZE = 5;

    private ModifierWrapper resources = new ModifierWrapper(0,0,0,0);

    protected String name;
    protected Stack<DominionCard> deck = new Stack<>();
    protected Stack<DominionCard> discard = new Stack<>();
    protected ArrayList<DominionCard> hand = new ArrayList<>();

    protected ArrayList<DominionCard> allCards = new ArrayList<>();

    public void setStartingDeck(){
        for(int i = 0; i < 5; i++){
            gainNewCard(new Copper());
        }

        for(int i = 0; i < 7; i++){
            gainNewCard(new Gold());
        }

        for(int i = 0; i < 3; i++){
            gainNewCard(new Cellar());
        }

        for(int i = 0; i < 3; i++){
            gainNewCard(new Estate());
        }

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
        drawCards(HAND_SIZE);
    }

    public void drawCards(int cards){
        for (int i = 0; i < cards; i++) {
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

    public void discardSpecificCard(DominionCard card){
        hand.remove(card);
        discard.add(card);
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

    public int getVictoryPoints(){
        return allCards.stream()
                .filter(c -> c.getCardType().equals(DominionCard.CardType.VICTORY))
                .map(c -> (Victory)c)
                .mapToInt(c -> c.VICTORY_POINTS)
                .sum();
    }

    public int totalCoinValue(boolean includeActionCards){
        // Returns the total coin value, optionally including +coin actions deck from the
        return deck.stream()
                .filter(c -> c.getCardType().equals(DominionCard.CardType.TREASURE) ||
                        (includeActionCards && c.getCardType().equals(DominionCard.CardType.ACTION)))
                .mapToInt(c -> c.turnBonusResources().coins)
                .sum();
    }

    public double averageCoinValue(boolean includeActionCards){
        return totalCoinValue(includeActionCards) / (double)(deck.size());
    }

    public List<DominionCard> acceptHandVisitor(HandVisitor visitor){
        return visitor.visit(this, hand);
    }

    public void gainNewCard(DominionCard card){
        allCards.add(card);
        discard.add(card);
    }

    /// Return the card you want to play
    public abstract DominionCard chooseAction(int actionsRemaining);

    /// Return a List of the cards you are buying this turn
    public abstract List<DominionCard> buyPhase(int coins);

}
