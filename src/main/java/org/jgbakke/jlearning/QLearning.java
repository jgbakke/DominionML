package org.jgbakke.jlearning;


import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class QLearning {

    private ActionContainer actionContainer;

    private HashMap<State, ActionScore[]> qTable;

    private double learningRate = 1;
    private double discountFactor = 0.9;
    private double randomChoiceChance = 0.1;

    public Action chooseAction(State currentState, Collection<Action> disallowedActions){
        // disallowedActions are provided by the client and should not be considered
        Action chosen = actionContainer.getAction(0);
        try {
            return chosen.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Action chooseAction(State currentState){
        return chooseAction(currentState, new LinkedList<>());
    }

    public QLearning(){
        loadQTable();
        actionContainer = ActionContainer.getInstance();
    }

    public static class QLearningBuilder {
        private QLearning qLearning = new QLearning();

        public void setLearningRate(double learningRate) {
            qLearning.learningRate = learningRate;
        }

        private void setDiscountFactor(double discountFactor) {
            qLearning.discountFactor = discountFactor;
        }

        private void setRandomChoiceChance(double randomChoiceChance) {
            qLearning.randomChoiceChance = randomChoiceChance;
        }

        public QLearning build(){
            return qLearning;
        }

    }

    private void loadQTable(){
        try(PostgresDriver pd = new PostgresDriver()){
            qTable = pd.loadQTable();
            System.out.println("qTable loaded");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}