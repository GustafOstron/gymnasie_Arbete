package src.GamePart;

import src.Neurals.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class NeuralTrainer {
    double mutationRate;
    int generationSize;
    int delay = 0;
    public File nnwSaveFile;
    public Game game = new Game();

    public NeuralTrainer(double mutationRate, int generationSize) throws IOException, InterruptedException {
        JFileChooser j = new JFileChooser("saveFiles/Networks");
        Scanner s = new Scanner(System.in);
        j.setDialogTitle("Choose Neural network to evolve");
        j.showOpenDialog(null);
        nnwSaveFile = j.getSelectedFile();
        if(!nnwSaveFile.exists()){
            nnwSaveFile.createNewFile();
            System.out.println("[nrOfInputs] [nrOfHiddenLayers] [hiddenLayerSize] [nrOfOutputs]");
            new NeuralNetworkV2(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt()).writeToFile(nnwSaveFile);
            System.out.println("Created new Neural network");
        }
        this.mutationRate = mutationRate;
        this.generationSize = generationSize;
    }

    public void setGameVis(boolean b){
        game.setGameVis(b);
    }

    public void setDelay(int ms){
        this.delay = ms;
    }

    public void doGen() throws InterruptedException, IOException, ClassNotFoundException {
        NeuralNetworkV2 bestNnw = NeuralNetworkV2.readNeuralNetworkFromFile(nnwSaveFile);
        int treat = 0;
        int highestTreat = getTotalTreat(bestNnw, game);
        List<NeuralNetworkV2> currentGen = createGen(bestNnw);
        for (int i = 0; i < currentGen.size(); i++) {
            treat = getTotalTreat(currentGen.get(i), game);
            if(treat >= highestTreat){
                highestTreat=game.getScore();
                bestNnw = currentGen.get(i);
            }
        }
        bestNnw.writeToFile(nnwSaveFile);
    }

    public int treat(Game game) {
        int treat = 0;
        int[][] playfield = game.getPlayingField();
        if (playfield[3][0] >= playfield[3][1]) {
            treat++;
        }
        if (Arrays.stream(playfield).flatMapToInt(Arrays::stream).max().getAsInt() == playfield[3][0]){
            treat++;
        }
        return treat;
    }

    public int getTotalTreat(NeuralNetworkV2 nnw, Game game) throws InterruptedException {
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
            totalTreat += treat(game);
            if (game.gameVis) {
                Thread.sleep(delay);
            }
            if (Arrays.deepEquals(previuosMovePlayingFeild, game.getPlayingField())){
                return totalTreat+game.getScore();
            }
        }
        return totalTreat+game.getScore();
    }

    public List<NeuralNetworkV2> createGen(NeuralNetworkV2 nnw){
        List<NeuralNetworkV2> l = new ArrayList<>();
        NeuralNetworkV2 temp = nnw;
        for (int i = 0; i < generationSize; i++) {
            temp = nnw;
            temp.mutate(mutationRate);
            l.add(temp);
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
