package src.GamePart;

import src.Neurals.NeuralNetwork;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static src.GamePart.NeuralTrainer.getTotalTreat;

public class MultiThreadNeuralTrainer extends NeuralTrainer{
    int highScore;
    ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public MultiThreadNeuralTrainer(double mutationRate, int generationSize) throws IOException, InterruptedException, ClassNotFoundException {
        super(mutationRate, generationSize);
        game = new GameWhithoutWindow();
        File myObj = new File("res/Game/highScore.txt");
        Scanner myReader = new Scanner(myObj);
        highScore = myReader.nextInt();
        myReader.close();
    }

    @Override
    public void doGen() throws InterruptedException, IOException, ClassNotFoundException, ExecutionException {
        NeuralNetwork bestNnw = previousBest;
        int highestScore = getTotalTreat(bestNnw, game, delay);
        int indexOfHighestScore = -1;
        NeuralNetwork[] currentGen = createGen(bestNnw);

        List<NnwPlayGame> npg = new ArrayList<>();
        for (int i = 0; i < generationSize; i++) {
            npg.add(new NnwPlayGame(currentGen[i]));
        }

        List<Future<Integer>> scores = service.invokeAll(npg);
        for (int i = 0; i < generationSize; i++) {
            if (scores.get(i).get() >= highestScore){
                highestScore = scores.get(i).get();
                indexOfHighestScore = i;
            }
        }
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


    public NeuralNetwork[] createGen(final NeuralNetwork nnw) throws InterruptedException, ExecutionException {
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
