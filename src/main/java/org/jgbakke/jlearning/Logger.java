package org.jgbakke.jlearning;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Logger implements Closeable {
    private static final int MINIMUM_LEVEL = LoggingSeverity.WARN.ordinal();

    private static final String RESET_COLOR ="\033[0m";
    private static final String RED_COLOR ="\033[0;31m";

    private static void printRed(String m){
        System.out.println(Logger.RED_COLOR + m + Logger.RESET_COLOR);
    }

    private final int id;

    private Connection conn;

    private PostgresDriver pd;

    public Logger(int id){
        pd = new PostgresDriver();
        conn = pd.establishConnection();
        this.id = id;
    }

    public void log(String message, LoggingSeverity severity){
        log(id, message, severity);
    }

    public void log(String message){
        log(id, message);
    }

    public void log(int gameID, String message){
        log(gameID, message, LoggingSeverity.INFO);
    }

    public void log(int gameID, String message, LoggingSeverity severity){
        if(severity.ordinal() < MINIMUM_LEVEL){
            return;
        }

        if(severity == LoggingSeverity.ERROR) {
            printRed(message);
        } else {
            System.out.println(message);
        }

        try{
            PreparedStatement statement = conn.prepareStatement("INSERT INTO logs " +
                    "(game_id, " +
                    "timestamp, " +
                    "severity, " +
                    "message) VALUES (" +
                    "?," +
                    "NOW()," +
                    "?," +
                    "?)"
            );

            statement.setInt(1, gameID);
            statement.setInt(2, severity.ordinal());
            statement.setString(3, message);

            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void close() {
        pd.close();
    }

    public enum LoggingSeverity {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
