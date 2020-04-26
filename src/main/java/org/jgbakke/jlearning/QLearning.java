package org.jgbakke.jlearning;

import java.sql.Connection;
import java.sql.DriverManager;

public class QLearning {
    private static void initDB(){
        // Run this in terminal to enable postgres command line tools
        //sudo mkdir -p /etc/paths.d && echo /Applications/Postgres.app/Contents/Versions/latest/bin | sudo tee /etc/paths.d/postgresapp

        Connection c = null;
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
        System.out.println("Opened database successfully");
    }

    public static void main(String[] args){
        System.out.println("Main method called");
        initDB();
    }
}
