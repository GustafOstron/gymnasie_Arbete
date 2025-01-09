package src;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DataLogger {
    private static final String url = "jdbc:mysql://localhost:3306/gymnasieprojekt";
    private static final String username = "java";
    private static final String password = "password";
    static Connection connection;
    static ExecutorService dbService = Executors.newFixedThreadPool(100);

    static {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
    }

    public static void writeGenToDB(int gen, int generationSize, double mutationRate, int hiddenLayers, int hiddenLayerSize, int genHighScore) throws IOException, SQLException {
        String sql = "INSERT INTO generations(Gen, GenerationSize, MutationRate, HiddenLayers, HiddenLayerSize, HighScore) VALUES(?,?,?,?,?,?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, gen);
        pst.setInt(2, generationSize);
        pst.setDouble(3, mutationRate);
        pst.setInt(4, hiddenLayers);
        pst.setInt(5, hiddenLayerSize);
        pst.setInt(6, genHighScore);
        pst.execute();
        pst.close();
    }

    public static void writeScoreToDb(int score) throws SQLException {
        String sql = "INSERT INTO scores(genid, score) VALUES(?,?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, genID);
        pst.setInt(2, score);
        pst.execute();
        pst.close();
    }

    public static void insertAllScores(int[] scores) throws SQLException {
        dbService.submit(new ScoreDbTask(scores, genID));
    }

    public static void insertAllScoresdddd(int[] scores, int genId) throws SQLException {
        String insertTableSQL = "INSERT INTO scores(genid, score) VALUES(?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
        try {
            connection.setAutoCommit(false);

            int batchTotal=0;
            for  (int i = 0; i < scores.length; i++) {
                preparedStatement.setInt(1, genId);
                preparedStatement.setInt(2, scores[i]);
                preparedStatement.addBatch();
                if (batchTotal++ == 4096) {
                    int[] result = preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                    batchTotal=0;
                }
            }
            if (batchTotal > 0) {
                preparedStatement.executeBatch();
            }
            connection.commit();
        }  finally {
            preparedStatement.close();
        }
    }
}
class ScoreDbTask implements Runnable {
    int[] scores;
    int gen;
    ScoreDbTask(int[] scores, int gen) {
        this.scores = scores;
        this.gen = gen;
    }

    @Override
    public void run() {
        try {
            DataLogger.insertAllScoresdddd(scores, gen);
        } catch (SQLException e) {
            System.out.println(e);
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
