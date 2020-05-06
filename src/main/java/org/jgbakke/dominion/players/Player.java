package org.jgbakke.dominion.players;

import org.jgbakke.dominion.DominionStateUpdater;
import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.HandVisitor;
import org.jgbakke.dominion.ModifierWrapper;
import org.jgbakke.dominion.actions.*;
import org.jgbakke.dominion.actions.Copper;
import org.jgbakke.dominion.actions.Treasure;
import org.jgbakke.dominion.actions.Estate;
import org.jgbakke.dominion.actions.Victory;
import org.jgbakke.jlearning.*;

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

    protected State currentState = new State(new DominionStateUpdater());

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

        DominionCard mine = null;
        DominionCard bureaucrat = null;
        DominionCard workshop = null;
        DominionCard throneRoom = null;
        DominionCard cellar = null;
        DominionCard mineableTreasure = null;

        int numPlayableCards = playableCards.size();

        for (DominionCard card : playableCards) {
            ModifierWrapper cardResources = card.turnBonusResources();

            if(card instanceof ThroneRoom){
                throneRoom = card;
            } else if (card instanceof Cellar){
                cellar = card;
            } else if (card instanceof Mine){
                mine = card;
            } else if (card instanceof Workshop){
                workshop = card;
            } else if (card instanceof Bureaucrat){
                bureaucrat = card;
            } else if (card instanceof Copper || card instanceof Silver){
                mineableTreasure = card;
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

        if(mine != null && mineableTreasure != null){
            return mine;
        }

        // If we got here than either we have actions AND no +card cards
        // or we have no actions and NO +action actions
        // so let's see which options gets us more money
        double expectedCoinValue = averageCoinValue(false);
        DominionCard maxNumCards = cardsOnly.isEmpty() ? null : cardsOnly.get(0);

        DominionCard maxCoinValue = playableCards.stream()
                .max(Comparator.comparingInt(c -> c.turnBonusResources().coins))
                .orElse(null);

        if(3 >= maxCoinValue.turnBonusResources().coins && 3 >= expectedCoinValue){
            if(bureaucrat != null){
                return bureaucrat;
            }

            if(workshop != null){
                return workshop;
            }
        }

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

    public abstract Action chooseBuy(State currentState, List<Action> validCards);

    /// Return a List of the cards you are buying this turn
    public List<DominionCard> buyPhase(int coins, int buys) {
        LinkedList<DominionCard> chosenBuys = new LinkedList<>();

        while(coins > 1 && buys > 0){
            List<Action> validCards = validBuyChoices(coins);
            DominionCard chosen = null;

            if(coins >= 8){
                chosen = new Province();
            } else if (coins >= 5 && game.getRemainingRounds() <= 3) {
                chosen = new Duchy();
            } else {
                Action qLearningAction = chooseBuy(currentState, validCards);

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

    public abstract void cleanup();

    public void logHand(){
        List<String> handContents = hand.stream().map(h -> h.toString().split("actions")[1]).collect(Collectors.toList());
        String logContent = String.join(" / ", handContents);
        logger.log(logContent);
    }

    private boolean canBuyCard(DominionCard card, int coins){
        return game.bank.hasCardsRemaining(card.id()) && card.cost() <= coins;
    }

    private boolean cardIsTooCheap(DominionCard card, int coins){
        return card.cost() < coins - 1;
    }

    protected List<Action> validBuyChoices(int coins){
        List<Action> validChoices = new LinkedList<>();

        for (Action action : ActionContainer.getInstance().getActions()) {
            DominionCard card = (DominionCard)action;

            if(canBuyCard(card, coins) && !cardIsTooCheap(card, coins)){
                validChoices.add(card);
            }
        }

        return validChoices;
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
