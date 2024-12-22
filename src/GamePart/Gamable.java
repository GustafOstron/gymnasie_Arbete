package src.GamePart;

public interface Gamable {
    public void reset();
    public int[][] getPlayingField();
    public boolean isLost();
    public void setPlayingField(int[][] playingField);
    public void moveUp();
    public void moveDown();
    public void moveRight();
    public void moveLeft();
    public int[][] moveLeft(int[][] field);
    public int[][] rotate(int[][] p, int rotations);
    public void addBlocks();
    public int getScore();
}
