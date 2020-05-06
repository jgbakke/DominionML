package org.jgbakke.jlearning;

import java.sql.SQLException;
import java.util.*;

public class QLearning {
    private List<Action> actionList;

    private ActionContainer actionContainer;

    private Random rand = new Random();

    private HashMap<State, ActionScore[]> qTable;

    private Reward reward;

    private double learningRate = 0.95;
    private double discountFactor = 0.95;
    private double randomChoiceChance = 0.1;

    private double defaultCellValue = 0;
    private double negativeModifier = 5;

    public QLearning(Reward reward){
        loadQTable();
        actionContainer = ActionContainer.getInstance();
        actionList = Arrays.asList(actionContainer.getActions());
        this.reward = reward;
    }


    // By default here, all actions are allowed
    public Action chooseAction(State currentState){
        return chooseAction(currentState, actionList);
    }

    // Pass allowed actions when some should be ineligible to perform.
    // For example, if you cannot afford to buy an action
    public Action chooseAction(State currentState, List<Action> allowedActions){
        if(allowedActions.isEmpty()){
            // If you are not allowed to buy anything just buy a copper
            return null;
        }

        Action chosen = Math.random() < randomChoiceChance ?
                chooseRandomAction(allowedActions) :
                chooseOptimalAction(currentState, allowedActions);

        if(chosen == null){
            return null;
        }

        return createNewInstance(chosen);
    }

    public Action chooseRandomAction(List<Action> allowedActions){
        if(allowedActions.isEmpty()){
            return null;
        }

        return allowedActions.get(rand.nextInt(allowedActions.size()));
    }

    public Action chooseOptimalAction(State current, Collection<Action> allowedActions){
        LinkedList<Action> optimalActions = new LinkedList<>();
        double maxScore = -1;

        for (Action action : allowedActions) {
            // State next = current.getResultingState(action);
            double nextScore = getScoreForAction(current, action);

            if(nextScore > maxScore){
                optimalActions = new LinkedList<>();
                optimalActions.add(action);
                maxScore = nextScore;
            } else if(nextScore == maxScore){
                optimalActions.add(action);
            }
        }

        // Choose a random to split the ties
        return chooseRandomAction(optimalActions);

//        return allowedActions.stream().max(Comparator.comparingDouble(act -> {
//            State next = current.getResultingState(act);
//            return maxScoreForState(next);
//        })).get();
    }

    private static Action createNewInstance(Action a){
        try {
            return a.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Use a QLearningBuilder if you want to override the default Learning parameters
    public static class QLearningBuilder {
        private QLearning qLearning;

        public QLearningBuilder(Reward reward){
            qLearning = new QLearning(reward);
        }

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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private double getScoreForAction(State current, Action action){
        if(!qTable.containsKey(current)){
            return 0;
        }

        try {
            return qTable.get(current)[action.id()].score;
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private double maxScoreForState(State s){
        if(!qTable.containsKey(s)){
            return 0;
        }

        List<ActionScore> scores = Arrays.asList(qTable.get(s));

        return scores.stream().max(Comparator.comparingDouble(scr -> scr.score)).get().score;
    }

    public void updateQTable(State beforeAction, Action action){
        double currentScore = getScoreForAction(beforeAction, action);
        double rewardValue = reward.getReward(action);
        double maxOfNext = maxScoreForState(beforeAction.getResultingState(action));

        double calculatedReward = (1 - learningRate) * currentScore + learningRate * (
                    rewardValue + discountFactor * maxOfNext - currentScore
                );

        setQTableCell(beforeAction, action, calculatedReward);
    }

    private void setQTableCell(State row, Action column, double newValue){
        if(!qTable.containsKey(row)){
            qTable.put(row, createEmptyQTableRow(row));
        }

        qTable.get(row)[column.id()].score = newValue;
    }

    private ActionScore[] createEmptyQTableRow(State row){
        ActionScore[] rowContent = new ActionScore[actionList.size()];
        for (int i = 0; i < rowContent.length; i++) {
            rowContent[i] = new ActionScore(actionList.get(i), defaultCellValue);
        }

        return rowContent;
    }

    public void saveToDB(){
        try(PostgresDriver pd = new PostgresDriver()) {
            pd.saveToDB(qTable);
        }
    }

}