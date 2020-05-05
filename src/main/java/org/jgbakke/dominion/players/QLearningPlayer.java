package org.jgbakke.dominion.players;

import org.jgbakke.dominion.DominionReward;
import org.jgbakke.dominion.DominionStateUpdater;
import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.actions.Copper;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.dominion.actions.Province;
import org.jgbakke.jlearning.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class QLearningPlayer extends Player {
    private Game game;
    private QLearning qLearning = new QLearning(new DominionReward());

    protected State currentState = new State(new DominionStateUpdater());

    public QLearningPlayer(Game game){
        this.game = game;
        this.name = "Dominionator";
    }

    @Override
    public DominionCard chooseAction(int actionsRemaining) {
        // TODO: More advanced implementation
        // Current algorithm: Just pick the first action card
        return hand.stream()
                .filter(c -> c.getCardType().equals(DominionCard.CardType.ACTION))
                .findFirst()
                .orElse(null);

    }

    @Override
    public List<DominionCard> buyPhase(int coins, int buys) {
        LinkedList<DominionCard> chosenBuys = new LinkedList<>();

        while(coins > 0 && buys > 0){
            List<Action> validCards = validBuyChoices(coins);

            DominionCard chosen;

            if(coins >= 8){
                chosen = new Province();
            } else {
                Action qLearningAction = qLearning.chooseAction(currentState, validCards);

                if(qLearningAction == null){
                    chosen = new Copper();
                } else {
                    chosen = (DominionCard)qLearningAction;
                }
            }

            chosenBuys.add(chosen);

            buys--;
            coins -= chosen.cost();
        }

        return chosenBuys;

    }

    @Override
    public void cleanup() {
        qLearning.saveToDB();
        saveGameResult(name, getVictoryPoints());
    }

    private void saveGameResult(String playerName, int score){
        try(PostgresDriver pd = new PostgresDriver()){
            Connection conn = pd.establishConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO games (player, score) VALUES (?, ?)");

            preparedStatement.setString(1, playerName);
            preparedStatement.setInt(2, score);

            preparedStatement.execute();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
