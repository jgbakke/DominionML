package org.jgbakke.jlearning;


import org.jgbakke.dominion.actions.Copper;

import java.sql.SQLException;
import java.util.*;

public class QLearning {
    private List<Action> actionList;

    private ActionContainer actionContainer;

    private Random rand = new Random();

    private HashMap<State, ActionScore[]> qTable;

    private double learningRate = 1;
    private double discountFactor = 0.9;
    private double randomChoiceChance = 1;


    // By default here, all actions are allowed
    public Action chooseAction(State currentState){
        return chooseAction(currentState, actionList);
    }

    // Pass allowed actions when some should be ineligible to perform.
    // For example, if you cannot afford to buy an action
    public Action chooseAction(State currentState, List<Action> allowedActions){
        if(allowedActions.isEmpty()){
            // If you are not allowed to buy anything just buy a copper
            return new Copper();
        }

        Action chosen = Math.random() < randomChoiceChance ?
                chooseRandomAction(allowedActions) :
                chooseOptimalAction(currentState, allowedActions);

        return createNewInstance(chosen);
    }

    public Action chooseRandomAction(List<Action> allowedActions){
        return allowedActions.get(rand.nextInt(allowedActions.size()));
    }

    public Action chooseOptimalAction(State current, Collection<Action> allowedActions){
        return allowedActions.stream().max(Comparator.comparingDouble(act -> {
            State next = current.getResultingState(act);
            return maxScoreForState(next);
        })).get();
    }

    private static Action createNewInstance(Action a){
        try {
            return a.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    public QLearning(){
        loadQTable();
        actionContainer = ActionContainer.getInstance();
        actionList = Arrays.asList(actionContainer.getActions());
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

    private int maxScoreForState(State s){
        if(!qTable.containsKey(s)){
            return 0;
        }

        List<ActionScore> scores = Arrays.asList(qTable.get(s));

        return scores.stream().max(Comparator.comparingInt(scr -> scr.score)).get().score;
    }

}