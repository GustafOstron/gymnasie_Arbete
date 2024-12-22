package src.GamePart;

import java.util.Arrays;
import java.util.Random;

public class GameWhithoutWindow implements Gamable{
    private int[][] playingField;
    int score;

    GameWhithoutWindow(){
        this.playingField = new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        this.score = 0;
    }

    public void reset(){
        playingField = new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        score = 0;
        addBlocks();
    }

    public int[][] getPlayingField() {
        return playingField;
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
        return true;
    }


    public void setPlayingField(int[][] playingField) {
        this.playingField = playingField;
    }


    public void moveUp() {
        setPlayingField(rotate(moveLeft(rotate(this.getPlayingField(), 3)), 1));
        addBlocks();
    }

    public void moveDown() {
        setPlayingField(rotate(moveLeft(rotate(this.getPlayingField(), 1)), 3));
        addBlocks();
    }

    public void moveRight() {
        setPlayingField(rotate(moveLeft(rotate(this.getPlayingField(), 2)), 2));
        addBlocks();
    }

    public void moveLeft() {
        setPlayingField(moveLeft(this.getPlayingField()));
        addBlocks();
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
                    }
                }
            }
        }
        return field;
    }

    public int[][] rotate(int[][] p, int rotations) {
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
