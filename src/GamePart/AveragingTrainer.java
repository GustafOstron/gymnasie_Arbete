package GamePart;

import Neurals.NeuralNetwork;

import java.io.IOException;
import java.util.List;

public class AveragingTrainer extends NeuralTrainer{
    public AveragingTrainer(double mutationRate, int generationSize, int fileNr) throws IOException, InterruptedException {
        super(mutationRate, generationSize, fileNr);
    }

    public AveragingTrainer(double mutationRate, int generationSize, NeuralNetwork nnw) throws IOException, InterruptedException {
        super(mutationRate, generationSize, nnw);
    }

    public AveragingTrainer(double mutationRate, int generationSize, int inputs, int hiddenLayers, int hiddenLayerSize, int outputs) throws IOException, InterruptedException {
        super(mutationRate, generationSize, inputs, hiddenLayers, hiddenLayerSize, outputs);
    }


    @Override
    public void doGen() throws InterruptedException {
        NeuralNetwork bestNnw = new NeuralNetwork(directoryRoot+"previousBest.txt");
        int highestTreat = getTotalTreat(bestNnw, game);
        List<NeuralNetwork> currentGen = createGen(bestNnw);
        for (int i = 0; i < currentGen.size(); i++) {
            int treat = 0;
            //do 10 times to get an avrage
            for (int j = 0; j < 10; j++) {
                treat += getTotalTreat(currentGen.get(i), game);
            }
            treat /= 10;
            if(treat >= highestTreat){
                highestTreat=game.getScore();
                bestNnw = currentGen.get(i);
            }
        }
        bestNnw.writeToFile(directoryRoot+"previousBest.txt");
    }


}
