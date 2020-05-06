package org.jgbakke.dominion.players;

import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.jlearning.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class BayesianPlayer extends Player {
    private Random rand = new Random();

    private boolean trainingMode = false;

    public BayesianPlayer(Game g) {
        super(g);
    }

    public BayesianPlayer(Game g, boolean trainingMode){
        this(g);
        this.trainingMode = trainingMode;
    }

    public Action chooseOptimalAction(List<Action> allowedActions){
        // TODO
        return null;
    }

    public Action chooseRandomAction(List<Action> allowedActions){
        if(allowedActions.isEmpty()){
            return null;
        }

        return allowedActions.get(rand.nextInt(allowedActions.size()));
    }

    @Override
    public Action chooseBuy(State currentState, List<Action> validCards) {
        if(trainingMode) {
            return chooseRandomAction(validCards);
        } else {
            return chooseOptimalAction(validCards);
        }
    }

    public double[] calculateBayesScores(){
        int actionsCount = ActionContainer.getInstance().getActionsCount();
        double[] scores = new double[actionsCount];

        for (DominionCard card : allCards) {
            if(card.id() >= 0) {
                scores[card.id()]++;
            }
        }

        int vp = getVictoryPoints();
        double totalCards = allCards.size();
        double bayesScoreModifier = vp / totalCards;

        for (int i = 0; i < scores.length; i++) {
            scores[i] *= bayesScoreModifier;
        }

        return scores;
    }

    @Override
    public void cleanup() {
        try(PostgresDriver pd = new PostgresDriver()) {
            Connection c = pd.establishConnection();
            double[] scores = calculateBayesScores();

            for (int i = 0; i < scores.length; i++) {
                PreparedStatement stmt = c.prepareStatement(
                        "INSERT INTO bayes_table (id, score) VALUES (?, ?) " +
                                "ON CONFLICT (id) DO UPDATE " +
                                "SET score=excluded.score + bayes_table.score;");

                stmt.setInt(1, i);
                stmt.setDouble(2, scores[i]);

                stmt.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
