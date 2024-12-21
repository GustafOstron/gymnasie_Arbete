package GamePart;

import Neurals.NeuralNetwork;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class EffectiverNeuralTrainer extends NeuralTrainer{
    double topdownScoreKofficient = 1;


    public EffectiverNeuralTrainer(double mutationRate, int generationSize, int fileNr) throws IOException, InterruptedException {
        super(mutationRate, generationSize, fileNr);
    }

    public EffectiverNeuralTrainer(double mutationRate, int generationSize, NeuralNetwork nnw) throws IOException, InterruptedException {
        super(mutationRate, generationSize, nnw);
    }

    public EffectiverNeuralTrainer(double mutationRate, int generationSize, int inputs, int hiddenLayers, int hiddenLayerSize, int outputs) throws IOException, InterruptedException {
        super(mutationRate, generationSize, inputs, hiddenLayers, hiddenLayerSize, outputs);
    }

    @Override
    public int getTotalTreat(NeuralNetwork nnw, Game game) throws InterruptedException {
        game.reset();
        while (!game.isLost()){
            int[][] previuosMovePlayingFeild = game.getPlayingField();
            List<Double> res = nnw.getAndCalculateOutputs(blockValueToNeuronInput(game.getPlayingField()));
            Queue<Double> move = new PriorityQueue<>();
            for (int i = 0; i < 4; i++) {
                move.add(res.get(i));
            }
            while(Arrays.deepEquals(previuosMovePlayingFeild, game.getPlayingField()) && !move.isEmpty()) {
                switch (res.indexOf(move.poll())) {
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
            if (game.gameVis) {
                Thread.sleep(10);
            }
            if (Arrays.deepEquals(previuosMovePlayingFeild, game.getPlayingField())){
                return effectiveScore(game);
            }
        }
        return effectiveScore(game);
    }

    private int effectiveScore(Game game){
        return (int)(Arrays.stream(game.getPlayingField()[3]).sum() * topdownScoreKofficient) + game.getScore();
    }

    public void setTopdownScoreKofficient(double topdownScoreKofficient) {
        this.topdownScoreKofficient = topdownScoreKofficient;
    }

    public double getTopdownScoreKofficient() {
        return topdownScoreKofficient;
    }
}
