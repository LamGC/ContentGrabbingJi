package net.lamgc.cgj.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RankingDB {

    private final Connection dbConnection;

    public RankingDB(String dbUrl, String username, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dbConnection = DriverManager.getConnection("jdbc:mysql://" + dbUrl + "/pixivRanking?useSSL=false&serverTimezone=UTC",username,password);
        checkAndFix(dbConnection);
    }

    private static void checkAndFix(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("");
    }


}
