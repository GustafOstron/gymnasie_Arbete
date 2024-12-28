package src;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataLogger {
    private static final String url = "jdbc:mysql://localhost:3306/gymnasieprojekt";
    private static final String username = "java";
    private static final String password = "password";

    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);

    }

    public static void writeGenToDB(int gen, int generationSize, double mutationRate, int hiddenLayers, int hiddenLayerSize) throws IOException, SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO generations(Gen, GenerationSize, MutationRate, HiddenLayers, HiddenLayerSize) VALUES(?,?,?,?,?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, gen);
            pst.setInt(2, generationSize);
            pst.setDouble(3, mutationRate);
            pst.setInt(4, hiddenLayers);
            pst.setInt(5, hiddenLayerSize);
            pst.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public static void writeScoreToDb(int score) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql;
            PreparedStatement pst;
            int gen;
            sql = "SELECT ID FROM generations ORDER BY ID DESC LIMIT 1";
            pst = connection.prepareStatement(sql);
            gen = pst.executeQuery().getInt(1);

            sql = "INSERT INTO scores(genid, score) VALUES(?,?)";
            pst = connection.prepareStatement(sql);
            pst.setInt(1, gen);
            pst.setInt(2, score);
            pst.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }
}
    /*
USE gymnasieprojekt;
CREATE TABLE generations(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Gen INT,
    GenerationSize INT,
    MutationRate INT,
    HiddenLayers INT,
    HiddenLayerSize INT
);
CREATE TABLE Scores(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    GenId INT REFERENCES generations(ID),
    SCORE INT
);

     LowestScore, AvrageScore, HighestScore,
     */
