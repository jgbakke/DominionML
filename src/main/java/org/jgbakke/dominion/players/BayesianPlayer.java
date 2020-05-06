package org.jgbakke.dominion.players;

import org.jgbakke.dominion.Game;
import org.jgbakke.dominion.actions.DominionCard;
import org.jgbakke.jlearning.*;

import java.sql.*;
import java.util.List;
import java.util.Random;

public class BayesianPlayer extends Player {
    private ActionContainer actionContainer = ActionContainer.getInstance();
    private Random rand = new Random();

    private boolean trainingMode = false;

    private ActionScore[] actionScores;

    public BayesianPlayer(Game g) {
        super(g);
        this.actionScores = generateActionScores();
    }

    public BayesianPlayer(Game g, boolean trainingMode){
        this(g);
        this.trainingMode = trainingMode;
    }

    // Returns the optimal actions ordered by their Bayes score
    private ActionScore[] generateActionScores(){
        try(PostgresDriver pd = new PostgresDriver()){
            Connection c = pd.establishConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT id, score from bayes_table order by id;");

            ActionScore[] actionsOrdering = new ActionScore[actionContainer.getActionsCount()];
            int index = 0;

            while(rs.next()){
                int actionID = rs.getInt(1);
                double actionScore = rs.getDouble(2);

                actionsOrdering[index++] = new ActionScore(actionID, actionScore);
            }

            return actionsOrdering;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public Action chooseOptimalAction(List<Action> allowedActions){
        if(allowedActions.isEmpty()){
            return null;
        }

        // This is O(N^2) but given that the maximum size of N = 12, this should perform better than the O(N) solution
        // because O(N) requires instantiation of a hashmap so that will likely outweigh the benefit from O(N)

        double[] weights = new double[actionScores.length];
        double weightsSum = 0;

        for (Action action : allowedActions) {
            double thisWeight = actionScores[action.id()].score;
            weights[action.id()] = thisWeight;
            weightsSum += thisWeight;
        }

        double randomVal = Math.random() * weightsSum;

        for (int i = 0; i < weights.length; i++) {
            if(randomVal < weights[i]){
                return QLearning.createNewInstance(actionContainer.getAction(i));
            }

            randomVal -= weights[i];
        }

        // You should not get here
        return null;


//        for (int actionId : optimalActionsOrdering) {
//            Action act = actionContainer.getAction(actionId);
//            if(allowedActions.contains(act)){
//                return act;
//            }
//        }
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
        if(!trainingMode){
            return;
        }

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
