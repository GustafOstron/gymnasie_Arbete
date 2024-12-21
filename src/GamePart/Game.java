package src.GamePart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Game extends JFrame {
    private int[][] playingField;
    int score, highScore;
    public boolean gameVis = true;

    public Game() {
        this.playingField = new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        this.setTitle("2048");
        this.setSize(800, 1000);
        this.setLayout(new GridLayout(5, 4, 5, 5));
        this.setIconImage(new ImageIcon("res/Game/2048Logo.png").getImage());
        this.score = 0;
        constructUI();

        try {
            File myObj = new File("res/Game/highScore.txt");
            Scanner myReader = new Scanner(myObj);
            highScore = myReader.nextInt();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e + " Highscore");
        }

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                File myObj = new File("res/Game/highScore.txt");
                try {
                    FileWriter myWriter = new FileWriter(myObj);
                    myWriter.write(highScore + "");
                    myWriter.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        };
        this.addWindowListener(exitListener);
    }

    Game(int[][] playingField) {
        this.playingField = playingField;
        this.setTitle("2048");
        this.setSize(800, 800);
        this.setLayout(new GridLayout(4, 4, 5, 5));
    }

    public void reset(){
        playingField = new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        score = 0;
        addBlocks();
    }

    public int[][] getPlayingField() {
        return playingField;
    }

    public void setGameVis(boolean gameVis) {
        this.gameVis = gameVis;
    }

    public boolean isLost(){
        if (Arrays.stream(playingField).flatMapToInt(Arrays::stream).anyMatch(i -> i == 0)) return false;
        for (int i = 0; i < 4; i++) {
            if (playingField[i][0] == playingField[i][1]) return false;
            if (playingField[i][1] == playingField[i][2]) return false;
            if (playingField[i][2] == playingField[i][3]) return false;
            if (playingField[0][i] == playingField[1][i]) return false;
            if (playingField[1][i] == playingField[2][i]) return false;
            if (playingField[2][i] == playingField[3][i]) return false;
        }
        File myObj = new File("res/Game/highScore.txt");
        try {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(highScore + "");
            myWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    void constructUI(){
        this.getContentPane().removeAll();
        for (int[] row : playingField) {
            for (int block : row) {
                this.add(new Tile(block));
            }
        }
        this.add(new JLabel());
        this.add(new JLabel("Score: " + score));
        this.add(new JLabel("High Score: " + highScore));
        this.add(new JLabel());
    }

    void setPlayingField(int[][] playingField){
        this.playingField = playingField;
        for (int i = 0; i < 16; i++) {
            ((Tile)this.getContentPane().getComponent(i)).setSize(playingField[i/4][i-(i/4)*4]);
        }
        ((JLabel)this.getContentPane().getComponent(17)).setText("Score: " + score);
        ((JLabel)this.getContentPane().getComponent(17)).setText("High Score: " + highScore);
    }

    public void moveUp() {
        setPlayingField(rotate(moveLeft(rotate(this.getPlayingField(), 3)), 1));
        addBlocks();
        this.setVisible(gameVis);
    }

    public void moveDown() {
        setPlayingField(rotate(moveLeft(rotate(this.getPlayingField(), 1)), 3));
        addBlocks();
        this.setVisible(gameVis);
    }

    public void moveRight() {
        setPlayingField(rotate(moveLeft(rotate(this.getPlayingField(), 2)), 2));
        addBlocks();
        this.setVisible(gameVis);
    }

    public void moveLeft() {
        setPlayingField(moveLeft(this.getPlayingField()));
        addBlocks();
        this.setVisible(gameVis);
    }

    public int[][] moveLeft(int[][] field) {
        boolean[] merged;
        for (int rad = 0; rad < 4; rad++) {
            merged = new boolean[]{false, false, false, false};
            for (int tile = 0; tile < 4; tile++) {
                for (int offset = 0; offset < tile; offset++) {
                    if (field[rad][tile - 1 - offset] == 0) {
                        field[rad][tile - 1 - offset] = field[rad][tile - offset];
                        field[rad][tile - offset] = 0;
                        merged[tile - 1 - offset] = merged[tile - offset];
                        merged[tile - offset] = false;
                    } else if ((field[rad][tile - 1 - offset] == field[rad][tile - offset]) &&
                            ((!merged[tile - offset]) &&
                                    (!merged[tile - 1 - offset]))) {
                        field[rad][tile - 1 - offset] *= 2;
                        field[rad][tile - offset] = 0;
                        merged[tile - 1 - offset] = true;
                        merged[tile - offset] = false;
                        this.score += field[rad][tile - 1 - offset];
                        if (this.score > this.highScore) {
                            this.highScore = this.score;
                            System.out.println("new highscore "+highScore);
                        }
                    }
                }
            }
        }
        return field;
    }

    int[][] rotate(int[][] p, int rotations) {
        int[][] m = p;
        int[][] n = new int[4][4];
        for (int i = 0; i < rotations; i++) {
            n = new int[4][4];
            for (int r = 0, cc = 4 - 1; r < 4; ++r, --cc)
                for (int c = 0, rr = 0; c < 4; ++c, ++rr)
                    n[rr][cc] = m[r][c];
            m = n;
        }
        return n;
    }

    public void addBlocks(){
        Random r = new Random();
        int[][] p = playingField;
        int index = 0;
        int tempTileValue;
        for (int i = 0; i < 1; i++) {
            tempTileValue = 1;
            if( Arrays.stream(p).flatMapToInt(Arrays::stream).anyMatch(in -> in == 0)) {
                while (tempTileValue != 0) {
                    index = r.nextInt(16);
                    tempTileValue = p[(index / 4) - ((index / 4) % 1)][index % 4];
                }

                if (r.nextInt(10) == 0) {
                    p[(index / 4) - ((index / 4) % 1)][index % 4] = 4;
                } else {
                    p[(index / 4) - ((index / 4) % 1)][index % 4] = 2;
                }
            }
        }
        setPlayingField(p);
    }

    public int getScore() {
        return score;
    }
}
