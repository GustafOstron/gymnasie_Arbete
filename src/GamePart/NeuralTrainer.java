package src.GamePart;

import src.Neurals.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class NeuralTrainer {
    double mutationRate;
    int generationSize;
    int delay = 0;
    NeuralNetwork previousBest;
    public File nnwSaveFile;
    public Gamable game = new Game();

    public NeuralTrainer(double mutationRate, int generationSize) throws IOException, ClassNotFoundException {
        JFileChooser j = new JFileChooser("saveFiles/Networks");
        Scanner s = new Scanner(System.in);
        j.setDialogTitle("Choose Neural network to evolve");
        j.showOpenDialog(null);
        nnwSaveFile = j.getSelectedFile();
        if(!nnwSaveFile.exists()){
            System.out.println("[nrOfInputs] [nrOfHiddenLayers] [hiddenLayerSize] [nrOfOutputs]");
            previousBest = new NeuralNetwork(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt());
            nnwSaveFile.createNewFile();
            previousBest.writeToFile(nnwSaveFile);
            System.out.println("Created new Neural network");
        }else {
            previousBest = NeuralNetwork.readNeuralNetworkFromFile(nnwSaveFile);
        }
        this.mutationRate = mutationRate;
        this.generationSize = generationSize;
    }

    public void setGameVis(boolean b){
        if (game.getClass() == Game.class) ((Game)game).setGameVis(b);
    }

    public void setDelay(int ms){
        this.delay = ms;
    }

    public void setGenerationSize(int generationSize) {
        this.generationSize = generationSize;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public void setPreviousBest(NeuralNetwork previousBest) {
        this.previousBest = previousBest;
    }

    public void setGame(Gamable game) {
        this.game = game;
    }

    public void setNnwSaveFile(File nnwSaveFile) {
        this.nnwSaveFile = nnwSaveFile;
    }

    public void done() throws IOException {
        previousBest.writeToFile(nnwSaveFile);
        System.exit(0);
    }

    public void doGen() throws InterruptedException, IOException, ClassNotFoundException, ExecutionException {
        NeuralNetwork bestNnw = previousBest;
        int treat = 0;
        int highestTreat = getTotalTreat(bestNnw, game, delay);
        NeuralNetwork[] currentGen = createGen(bestNnw);
        for (int i = 0; i < currentGen.length; i++) {
            treat = getTotalTreat(currentGen[i], game, delay);
            if(treat >= highestTreat){
                highestTreat=game.getScore();
                bestNnw = currentGen[i];
            }
        }
        previousBest = bestNnw;
        previousBest.writeToFile(nnwSaveFile);
    }

    public static int getTotalTreat(NeuralNetwork nnw, Gamable game, int delay) throws InterruptedException {
        game.reset();
        int totalTreat = 0;
        while (!game.isLost()){
            int[][] previuosMovePlayingFeild = game.getPlayingField();
            double[] result = nnw.getOutput(blockValueToNeuronInput(game.getPlayingField()));
            Queue<Double> move = new PriorityQueue<>();
            for (int i = 0; i < 4; i++) {
                move.add(result[i]);

            }
            while(Arrays.deepEquals(previuosMovePlayingFeild, game.getPlayingField()) && !move.isEmpty()) {
                int d = findIndex(result, move.poll());
                switch (d) {
                    case 0:
                        game.moveRight();
                        break;
                    case 1:
                        game.moveLeft();
                        break;
                    case 2:
                        game.moveUp();
                        break;
                    case 3:
                        game.moveDown();
                        break;
                }
            }
            if (Arrays.deepEquals(previuosMovePlayingFeild, game.getPlayingField())){
                return game.getScore();
            }
        }
        return game.getScore();
    }

    public NeuralNetwork[] createGen(final NeuralNetwork nnw) throws InterruptedException, ExecutionException {
        NeuralNetwork[] l = new NeuralNetwork[generationSize];
        for (int i = 0; i < generationSize; i++) {
            NeuralNetwork temp = new NeuralNetwork(nnw);
            l[i] = temp;
            l[i].mutate(mutationRate);
        }
        return l;
    }

    public static double[] blockValueToNeuronInput(int[][] values){
        double[] out = Arrays.stream(values).flatMapToInt(IntStream::of).mapToDouble(Double::valueOf).toArray();
        for (int i = 0; i < out.length; i++) {
            if (out[i]!=0){
                out[i] = ((Math.log(out[i]) / Math.log(2)) / 17)+1;
            }
        }
        return out;
    }

    public static int findIndex(double[] array, double target) {
        // Loop through the array
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i; // Return the index if found
            }
        }
        return -1; // Return -1 if not found
    }
}
