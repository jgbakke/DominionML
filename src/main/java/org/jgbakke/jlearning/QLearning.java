package org.jgbakke.jlearning;


public class QLearning {

    private double learningRate = 1;
    private double discountFactor = 0.9;
    private double randomChoiceChance = 0.1;

    public static void main(String[] args){
        System.out.println("Main method called");
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

}