package org.jgbakke.jlearning;

public class ActionScore {
    public final Action action;
    public double score;

    public ActionScore(Action action, double score) {
        this.action = action;
        this.score = score;
    }

    public ActionScore(int actionID, double score){
        this(ActionContainer.getInstance().getActionById(actionID), score);
    }

    public void updateScore(double s){
        score = s;
    }


}
