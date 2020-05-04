package org.jgbakke.jlearning;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// This class is designed to setup the database on a new machine
public class PostgresSetup {

    public static void main(String[] args){
        initDB();
    }

    // Set up the DB if it does not exist yet
    public static void initDB(){
        try(PostgresDriver postgresDriver = new PostgresDriver()) {
            try {
                Connection conn = postgresDriver.establishConnection();
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS qtable (states integer[], action_scores decimal[]);");
                stmt.execute("CREATE TABLE IF NOT EXISTS logs (game_id integer, timestamp timestamp, severity smallint, message text)");
                stmt.execute("SET TIMEZONE='America/Chicago'");
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
