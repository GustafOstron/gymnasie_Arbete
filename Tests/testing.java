package Tests;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class testing {
    public static void main(String[] args) throws IOException {
        JFileChooser j = new JFileChooser("saveFiles/Networks");
        j.setDialogTitle("Choose Neural network to evolve");
        j.showOpenDialog(null);
        File f = j.getSelectedFile();
        System.out.println(j.getSelectedFile().getAbsoluteFile());

    }
}
