package org.jgbakke.jlearning;

import java.io.Closeable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PostgresDriver implements Closeable {
    private Connection c;

    public PostgresDriver(){
        establishConnection();
    }

    public Connection getDBConnection() {
        return c;
    }

    public Connection establishConnection(){
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/qlearning",
                            "postgres", "123");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        return c;
    }

    public HashMap<State, ActionScore[]> loadQTable() throws SQLException {
        HashMap<State, ActionScore[]> qTable = new HashMap<>();

        try(Statement stmt = c.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM qtable ORDER BY states;");
            while(rs.next()){
                Array state = rs.getArray(1);
                Array actionScores = rs.getArray(2);

                State s = new ImmutableState(loadPostgresIntArray(state));
                ActionScore[] actionScoresArr = new ActionScore[ActionContainer.getInstance().getActionsCount()];

                double[] scores = loadPostgresDoubleArray(actionScores);

                for (int actId = 0; actId < scores.length; actId++) {
                    actionScoresArr[actId] = new ActionScore(actId, scores[actId]);
                }

                qTable.put(s, actionScoresArr);
            }
        }

        return qTable;
    }

    public static int[] loadPostgresIntArray(Array arr) throws SQLException {
        return Arrays.stream((Integer[])arr.getArray()).mapToInt(Integer::intValue).toArray();
    }

    public static double[] loadPostgresDoubleArray(Array arr) throws SQLException {
        BigDecimal[] objArray = (BigDecimal[])arr.getArray();
        double[] doubleArray = new double[objArray.length];
        for (int i = 0; i < objArray.length; i++) {
            doubleArray[i] = objArray[i].doubleValue();
        }

        return doubleArray;
    }

    public void saveToDB(HashMap<State, ActionScore[]> qTable){
        qTable.forEach((State row, ActionScore[] action) -> {
            try {
                PreparedStatement stmt = c.prepareStatement(
                        "INSERT INTO qtable (states, action_scores) VALUES (?, ?) " +
                            "ON CONFLICT (states) DO UPDATE " +
                                "SET states=excluded.states, action_scores=excluded.action_scores;");
                Array statesArray = row.getStateAsArray(c);
                Array actionsArray =  ActionScore.getScoresAsArray(c, action);

                stmt.setArray(1, statesArray);
                stmt.setArray(2, actionsArray);

                stmt.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
    }

    @Override
    public void close() {
        //System.out.println("Closing Postgres Driver");

        try {
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
