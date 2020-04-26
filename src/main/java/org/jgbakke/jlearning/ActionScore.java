package org.jgbakke.jlearning;

public class ActionScore {
    private Action action;
    private int score;

    public ActionScore(Action action, int score) {
        this.action = action;
        this.score = score;
    }

    public ActionScore(int actionID, int score){
        this(ActionContainer.getInstance().getActionById(actionID), score);
    }


}
