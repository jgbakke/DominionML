package org.jgbakke.dominion;

import org.jgbakke.dominion.actions.ActionRequest;
import org.jgbakke.dominion.actions.ActionResponse;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.players.BayesianPlayer;
import org.jgbakke.dominion.players.Player;
import org.jgbakke.jlearning.Logger;
import org.jgbakke.jlearning.PostgresDriver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class Game {
    public final Bank bank;

    private int id;

    private int maxRounds;

    private int round = 0;

    private Player[] players;

    public Logger logger;

    public static void initDB(){
        try(PostgresDriver pd = new PostgresDriver()){
            Statement stmt = pd.establishConnection().createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS games" +
                    " (id SERIAL PRIMARY KEY, player TEXT, score INTEGER);");

            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args){
        //initDB();

        for(int i = 1500; i < 3000; i++) {
            try {
                Game g = new Game(i, 1, 19);
                g.startGame();
            } catch (Exception e){
                Logger errLogger = new Logger(i);
                errLogger.log("THERE WAS A CRITICAL ERROR: " + e.getMessage(), Logger.LoggingSeverity.ERROR);
                errLogger.close();

                e.printStackTrace();
                i--;
            }
        }
    }

    public int getVictoryPoints(int playerId){
        return players[playerId].getVictoryPoints();
    }

    public Game(int id, int players, int maxRounds){
        this.id = id;
        this.logger = new Logger(id);
        this.players = createPlayers(players);
        this.maxRounds = maxRounds;
        this.bank = new Bank();
    }

    public void initPlayers(){
        for (Player player : players) {
            player.setStartingDeck();
            player.drawHand();
        }
    }

    public void startGame(){
        long start = System.nanoTime();

        initPlayers();

        takeTurns();
        cleanup();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        double ms = elapsed / Math.pow(10, 6);
        players[0].saveGameResult("AI", players[0].getVictoryPoints());
        logger.log(String.format("TOTAL VP: %d", players[0].getVictoryPoints()), Logger.LoggingSeverity.WARN);
        logger.log(String.format("GAME ENDED IN %.2f MS", ms), Logger.LoggingSeverity.WARN);
        logger.close();
    }

    private void takeTurns(){
        for(round = 0; round < maxRounds; round++){
            for (Player player : players) {
                takePlayerTurn(player);
            }
        }
    }

    private void cleanup(){
        for (Player player : players) {
            player.cleanup();
        }
    }

    public void playCard(Player p, DominionCard card){
        logger.log("Playing a " + card.toString());
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

    public String takeAiPlayerTurn(){
        return takePlayerTurn(players[0]);
    }

    private String takePlayerTurn(Player p){
        p.resetResources();

        logger.log("STARTING PLAYER ACTION PHASE");
        p.logHand();

        int cardsPlayedThisTurn = 0;

        // You should never play this many so lets check this just to avoid infinite looops
        while(p.getResources().actions > 0 && cardsPlayedThisTurn++ < 50){

            // They played an action so reduce it by 1
            p.combineResources(new ModifierWrapper(-1, 0, 0, 0));

            // Pick a card
            DominionCard chosenCard = p.chooseAction(p.getResources());

            if(chosenCard == null){
                break;
            }

            playCard(p, chosenCard);
        }

        logger.log("STARTING PLAYER BUY PHASE");

        // Now lay our coins on the table so everybody can see
        // "KEITH SHOW YOUR CARDS!!!"
        p.addTreasureToModifiers();

        logger.log("Treasure value: " + p.getResources().coins);
        buyPhase(p, p.getResources());

        p.discardHand();
        p.drawHand();

        logger.log("END TURN");
        return "End turn";
    }

    public void buyPhase(Player p, ModifierWrapper resourcesAvailable){
        List<DominionCard> chosenCards = p.buyPhase(resourcesAvailable.coins, resourcesAvailable.buys);
        giveCardsToPlayer(chosenCards, p);

        chosenCards.forEach(c -> logger.log("Bought a " + c.toString()));
    }

    private void giveCardsToPlayer(List<DominionCard> cards, Player p){
        cards.forEach( c -> {
            bank.takeCard(c.id());
            p.gainNewCard(c);
        });
    }

    public int getRound() {
        return round;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public int getRemainingRounds(){
        return getMaxRounds() - getRound();
    }

    private Player[] createPlayers(int num){
        players = new Player[num];

        if(num == 1){
            players[0] = new BayesianPlayer(this, false);
        } else {
            throw new NotImplementedException();
        }

        return players;
    }
}
