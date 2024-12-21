package src;

import src.GamePart.*;


import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException{
        Scanner s = new Scanner(System.in);

        //NeuralTrainer nt = new NeuralTrainer(0.25, 250, new NeuralNetwork(16,5,100,4));
        NeuralTrainer nt = new NeuralTrainer(0.25, 250,10);
        System.out.println(nt.getNnwSize());
        nt.setGameVis(true);
        for (int i = 0; i < 1000000; i++) {
            nt.doGen();
            System.out.println("gen "+(i)+" done");
        }

         /*

        AveragingTrainer ant = new AveragingTrainer(0.25 , 1000, new NeuralNetwork(16,5,50,4));
        AveragingTrainer ant = new AveragingTrainer(0.25, 100,10);
        System.out.println(ant.getNnwSize());
        ant.setGameVis(true);
        for (int i = 0; i < 1000000; i++) {
            ant.doGen();
            System.out.println("gen "+(i)+" done");
        }

         */


        /*
        //EffectiverNeuralTrainer efnt = new EffectiverNeuralTrainer(25000, 250, new NeuralNetwork(16,5,100,4));
        EffectiverNeuralTrainer efnt = new EffectiverNeuralTrainer(25000, 250,9);
        System.out.println(efnt.getNnwSize());
        efnt.setTopdownScoreKofficient(4);
        efnt.setGameVis(true);
        for (int i = 0; i < 1000000; i++) {
            efnt.doGen();
            System.out.println("gen "+(i)+" done");
        }

         */

    }

    //träna på avrage score
}