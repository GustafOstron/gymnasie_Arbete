package src;

import java.io.IOException;
import java.sql.*;


public class DataLogger {
    private static final String url = "jdbc:mysql://localhost:3306/gymnasieprojekt";
    private static final String username = "java";
    private static final String password = "password";
    private static Connection connection;
    private static String sql = "INSERT INTO generations(Gen, GenerationSize, MutationRate, HiddenLayers, HiddenLayerSize, HighScore) VALUES(?,?,?,?,?,?)";
    private static PreparedStatement pst;

    static {
        try {
            connection = DriverManager.getConnection(url, username, password);
            pst = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        DriverManager.getConnection(url, username, password);
    }

    public static void addGenToBatch(int gen, int generationSize, double mutationRate, int hiddenLayers, int hiddenLayerSize, int genHighScore) throws IOException, SQLException {
        pst.setInt(1, gen);
        pst.setInt(2, generationSize);
        pst.setDouble(3, mutationRate);
        pst.setInt(4, hiddenLayers);
        pst.setInt(5, hiddenLayerSize);
        pst.setInt(6, genHighScore);
        pst.addBatch();
    }

    public static void sendBatch() throws SQLException {
        pst.executeBatch();
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
