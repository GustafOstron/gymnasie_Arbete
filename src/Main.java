package src;

import src.GamePart.*;


import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, ExecutionException {
        Scanner s = new Scanner(System.in);

        MultiThreadNeuralTrainer nt = new MultiThreadNeuralTrainer(0.1, 100000);
        nt.setGameVis(false);
        nt.setDelay(300);
        for (int i = 0; i < 100000; i++) {
            nt.doGen();
            System.out.println("gen " + (i) + " done");
        }
        nt.done();
    }
}