package src;

import src.GamePart.*;
import src.Neurals.NeuralNetwork;


import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, ExecutionException {
        Scanner s = new Scanner(System.in);

        NeuralTrainer nt = new NeuralTrainer(0.5, 10000);
        nt.setGameVis(false);
        nt.setDelay(300);
        nt.networkPlayAGame();
        /*
        for (int i = 0; i < 10000000; i++) {
            nt.doGen();
            System.out.println("gen " + (i) + " done");
        }

         */
        nt.done();
    }

    public static void exeperiment() throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        NeuralTrainer nt = new NeuralTrainer(0, 10000);
        for (double mutationRate = 0.05; mutationRate <= 0.5; mutationRate+=0.05) {
            nt.setMutationRate(mutationRate);
            for (int lager = 1; lager <= 10; lager++) {
                for (int nevroner = 1; nevroner <= 64; nevroner++) {
                    nt.setPreviousBest(new NeuralNetwork(16,lager,nevroner,4));
                    for (int gen = 1; gen <= 100; gen++) {
                        nt.doGen();
                    }
                }
            }
        }
    }
}