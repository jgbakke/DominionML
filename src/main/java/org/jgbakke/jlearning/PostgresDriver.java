package org.jgbakke.jlearning;

import java.io.Closeable;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;

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
            ResultSet rs = stmt.executeQuery("SELECT * FROM qtable;");
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
        return Arrays.stream((Double[])arr.getArray()).mapToDouble(Double::doubleValue).toArray();
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
