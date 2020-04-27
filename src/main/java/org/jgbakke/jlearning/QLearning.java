package org.jgbakke.jlearning;


import java.sql.SQLException;
import java.util.HashMap;

public class QLearning {

    private HashMap<State, ActionScore[]> qTable;

    private double learningRate = 1;
    private double discountFactor = 0.9;
    private double randomChoiceChance = 0.1;

    public static void main(String[] args){
         //QLearning ql = new QLearning();
    }

    public QLearning(){
        loadQTable();
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