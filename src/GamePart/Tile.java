package src.GamePart;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;

class Tile extends JButton {
    int size;

    Tile(int size) {
        this.size = size;
        this.setBackground(getColor());
        this.setEnabled(false);
        this.setFont(new Font("2", Font.BOLD, 50));
        this.setUI(new MetalButtonUI() {
            protected Color getDisabledTextColor() {
                return Color.BLACK;
            }
        });
        if (size != 0) {
            this.setText("" + size);
        }
    }

    public void setSize(int size){
        this.size = size;
        this.setBackground(getColor());
        if (size != 0) {
            this.setText("" + size);
        }else{
            this.setText("");
        }
    }

    public int getSizeOfTile(){
        return size;
    }


    private Color getColor() {
        return Color.decode(switch (size) {
            case 0, 2 -> "#eee4da";
            case 4 -> "#ede0c8";
            case 8 -> "#f2b179";
            case 16 -> "#f59563";
            case 32 -> "#f67c5f";
            case 64 -> "#f65e3b";
            case 128 -> "#edcf72";
            case 256 -> "#edcc61";
            case 512 -> "#edcc850";
            case 1024 -> "#edc53f";
            case 2048 -> "#edc22e";
            default -> "#4242f5";
        });
    }
}
