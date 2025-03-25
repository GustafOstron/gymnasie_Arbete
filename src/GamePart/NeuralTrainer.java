package src.GamePart;

import src.Neurals.*;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;
import static src.GamePart.NeuralTrainer.getTotalTreat;

public class NeuralTrainer {
    double mutationRate;
    int generationSize;
    int delay = 0;
    int genHighscore;
    int highScore;
    ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    NeuralNetwork previousBest;
    public File nnwSaveFile;
    public Gamable game = new GameWhithoutWindow();
    boolean multiThread = false;
    boolean averagePlay = false;
    int nrAvragePlaye = 10;

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
        File myObj = new File("res/Game/highScore.txt");
        Scanner myReader = new Scanner(myObj);
        highScore = myReader.nextInt();
        myReader.close();
        this.mutationRate = mutationRate;
        this.generationSize = generationSize;
    }

    public void setGameVis(boolean b){
        if (b) {
            if (multiThread) {
                multiThread = false;
                game = new Game();
                ((Game)game).setGameVis(true);
            }else {
                ((Game)game).setGameVis(true);
            }
        }else{
            if (game.getClass() == Game.class) ((Game)game).setGameVis(b);
        }
    }

    public void setMultiThread(boolean b) {
        game = new GameWhithoutWindow();
        this.multiThread = b;
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

    public int getGenHighscore() {
        return genHighscore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setAveragePlay(boolean averagePlay) {
        this.averagePlay = averagePlay;
    }

    public void setNrAvragePlaye(int nrAvragePlaye) {
        this.nrAvragePlaye = nrAvragePlaye;
    }

    public void done() throws IOException {
        previousBest.writeToFile(nnwSaveFile);
        System.exit(0);
    }

    public void doGen() throws SQLException, IOException, ExecutionException, InterruptedException, ClassNotFoundException {
        if(multiThread){
            doMultiThreadGen();
        }else {
            doSingleThreadGen();
        }
    }

    public void doMultiThreadGen() throws InterruptedException, IOException, ClassNotFoundException, ExecutionException, SQLException {
        NeuralNetwork bestNnw = previousBest;
        int highestScore = getTotalTreat(bestNnw, game, delay);
        int indexOfHighestScore = -1;
        NeuralNetwork[] currentGen = createGenMultiThread(bestNnw);

        List<Future<Integer>> scores;

        if (averagePlay){
            List<NnwPlayGameAverage> npg = new ArrayList<>();
            for (int i = 0; i < generationSize; i++) {
                npg.add(new NnwPlayGameAverage(currentGen[i], nrAvragePlaye));
            }
            scores = service.invokeAll(npg);
        }else {
            List<NnwPlayGame> npg = new ArrayList<>();
            for (int i = 0; i < generationSize; i++) {
                npg.add(new NnwPlayGame(currentGen[i]));
            }
            scores = service.invokeAll(npg);
        }


        for (int i = 0; i < generationSize; i++) {
            int score = scores.get(i).get();
            if (score >= highestScore){
                highestScore = score;
                indexOfHighestScore = i;
            }
        }
        genHighscore = highestScore;

        if (indexOfHighestScore != -1){
            bestNnw = currentGen[indexOfHighestScore];
            if (scores.get(indexOfHighestScore).get() > highScore){
                highScore = scores.get(indexOfHighestScore).get();
                System.out.println("new highScore " + highestScore);
            }else {
                System.out.println("gen highscore " + scores.get(indexOfHighestScore).get());
            }
        }
        previousBest = bestNnw;
        previousBest.writeToFile(nnwSaveFile);

        File myObj = new File("res/Game/highScore.txt");
        FileWriter myWriter = new FileWriter(myObj);
        myWriter.write(highScore + "");
        myWriter.close();
    }

    public void doSingleThreadGen() throws InterruptedException, IOException, ClassNotFoundException, ExecutionException, SQLException {
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
        System.out.println("gen highscore " + highestTreat);
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
            if (delay>0){
                sleep(delay);
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
            l[i] =  new NeuralNetwork(nnw);
            l[i].mutate(mutationRate);
        }
        return l;
    }

    public NeuralNetwork[] createGenMultiThread(final NeuralNetwork nnw) throws InterruptedException, ExecutionException {
        List<MutateNetwork> tasks = new ArrayList<>();
        for (int i = 0; i < generationSize; i++) {
            tasks.add(new MutateNetwork(nnw, mutationRate));
        }
        List<Future<NeuralNetwork>> li = service.invokeAll(tasks);
        NeuralNetwork[] l = new NeuralNetwork[generationSize];
        for (int i = 0; i < generationSize; i++) {
            l[i] = li.get(i).get();
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

    public void networkPlayAGame() throws InterruptedException {
        game.reset();
        setGameVis(true);
        while (!game.isLost()){
            sleep(500);
            int[][] previuosMovePlayingFeild = game.getPlayingField();
            double[] result = previousBest.getOutput(blockValueToNeuronInput(game.getPlayingField()));
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
                wait(5000);
                return;
            }
        }
        sleep(20000);
        //wait(5000);
        return;
    }

}


class NnwPlayGame implements Callable<Integer> {
    NeuralNetwork nnw;
    Gamable game = new GameWhithoutWindow();

    NnwPlayGame(NeuralNetwork nnw){
        this.nnw = nnw;
    }
    @Override
    public Integer call() throws Exception {
        return getTotalTreat(nnw, game, 0);
    }
}

class NnwPlayGameAverage implements Callable<Integer> {
    NeuralNetwork nnw;
    Gamable game = new GameWhithoutWindow();
    int timesPlay;

    NnwPlayGameAverage(NeuralNetwork nnw, int timesPlay){
        this.nnw = nnw;
        this.timesPlay = timesPlay;
    }
    @Override
    public Integer call() throws Exception {
        int avrageScore = 0;
        for (int i = 0; i < timesPlay; i++) {
            avrageScore += getTotalTreat(nnw, game, 0);
        }
        return (avrageScore / timesPlay);
    }
}

class MutateNetwork implements Callable<NeuralNetwork> {
    NeuralNetwork nnw;
    double mutationRate;

    MutateNetwork(NeuralNetwork nnw, double mutationRate){
        this.nnw = nnw;
        this.mutationRate = mutationRate;
    }
    @Override
    public NeuralNetwork call() throws Exception {
        NeuralNetwork temp = new NeuralNetwork(nnw);
        temp.mutate(mutationRate);
        return temp;
    }
}