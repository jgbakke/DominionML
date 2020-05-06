package org.jgbakke.dominion.players;

import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.HandVisitor;
import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.*;
import org.jgbakke.dominion.actions.Copper;
import org.jgbakke.dominion.actions.Treasure;
import org.jgbakke.dominion.actions.Estate;
import org.jgbakke.dominion.actions.Victory;
import org.jgbakke.jlearning.Logger;
import org.jgbakke.jlearning.PostgresDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Player {
    private static final int HAND_SIZE = 5;

    private ModifierWrapper resources = new ModifierWrapper(0,0,0,0);

    protected String name;
    protected Stack<DominionCard> deck = new Stack<>();
    protected Stack<DominionCard> discard = new Stack<>();
    protected ArrayList<DominionCard> hand = new ArrayList<>();

    protected ArrayList<DominionCard> allCards = new ArrayList<>();

    public Logger logger;

    protected Game game;

    public Player(Game g){
        this.game = g;
        this.logger = game.logger;
    }

    public void setStartingDeck(){
        for(int i = 0; i < 7; i++){
            gainNewCard(new Copper());
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

    public void addOnTopOfDeck(DominionCard card){
        deck.push(card);
    }

    protected void shuffleDeck(){
        Collections.shuffle(discard);
        deck = discard;
        discard = new Stack<>();
    }

    public void trashCardFromHand(DominionCard card){
        hand.remove(card);
        trashCard(card);
    }

    public void putCardDirectlyInHand(DominionCard card){
        // Puts the card right into their hand without shuffling or any other rule following
        logger.log("Putting directly into hand a " + card.toString());
        hand.add(card);
        allCards.add(card);
    }

    protected void addCardToHand(){
        if(deck.empty()){
            shuffleDeck();
        }

        if(!deck.empty()) {
            // It is possible for it to still be empty
            // in the case where every single card is on the table
            hand.add(deck.pop());
        }
    }

    protected void trashCard(DominionCard card){
        allCards.remove(card);
        logger.log("Trashed a " + card);
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
                .mapToInt(c -> ((Victory)c).VICTORY_POINTS)
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
    public abstract DominionCard chooseAction(ModifierWrapper currentResources);

    /// Return a List of the cards you are buying this turn
    public abstract List<DominionCard> buyPhase(int coins, int buys);

    public abstract void cleanup();

    public void logHand(){
        List<String> handContents = hand.stream().map(h -> h.toString().split("actions")[1]).collect(Collectors.toList());
        String logContent = String.join(" / ", handContents);
        logger.log(logContent);
    }

    public void saveGameResult(String playerName, int score){
        try(PostgresDriver pd = new PostgresDriver()){
            Connection conn = pd.establishConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO games (player, score) VALUES (?, ?)");

            System.out.println("THE SCORE IS " + score);
            preparedStatement.setString(1, playerName);
            preparedStatement.setInt(2, score);

            preparedStatement.execute();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
