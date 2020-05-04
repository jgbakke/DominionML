package org.jgbakke.jlearning;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

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

    public static Array getScoresAsArray(Connection c, ActionScore[] actions) throws SQLException {
        return c.createArrayOf("float4", Stream.of(actions).map(i -> (Object)(i.score)).toArray());
    }


}
