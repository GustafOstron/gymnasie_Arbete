package GamePart;

import Neurals.NeuralNetwork;
import Neurals.Neuron;
import Neurals.NeuronLayer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NeuralTrainer {
    int mutationRate, generationSize;
    String directoryRoot = "C:/Users/gustafcarl.ostrom/Min enhet/Programering Tillämpad/gymnasieprojekt/src/";
    public Game game = new Game();

    public NeuralTrainer(double mutationRate, int generationSize, int fileNr) throws IOException, InterruptedException {
        directoryRoot += "nnwsV2/nnw"+fileNr+"/";
        this.mutationRate = (int)mutationRate * neuronsInNetwork(new NeuralNetwork(directoryRoot+"previousBest.txt"));
        this.generationSize = generationSize;
    }
    public NeuralTrainer(double mutationRate, int generationSize, NeuralNetwork nnw) throws IOException, InterruptedException {
        this.mutationRate = (int)mutationRate * neuronsInNetwork(nnw);
        this.generationSize = generationSize;
        fileGenerator();
        nnw.writeToFile(directoryRoot+"previousBest.txt");
    }
    public NeuralTrainer(double mutationRate, int generationSize, int inputs, int hiddenLayers, int hiddenLayerSize, int outputs) throws IOException, InterruptedException {
        NeuralNetwork nnw = new NeuralNetwork(inputs, hiddenLayers, hiddenLayerSize, outputs);
        nnw.writeToFile(directoryRoot+"previousBest.txt");
        this.mutationRate = (int)mutationRate * neuronsInNetwork(nnw);
        this.generationSize = generationSize;
        fileGenerator();
    }

    private int neuronsInNetwork(NeuralNetwork nnw){
        int totalNeurons = 0;
        for (NeuronLayer nl:nnw.getNeuralLayers()){
            for (Neuron n: nl.getNeurons()){
                totalNeurons++;
            }
        }
        return totalNeurons;
    }

    public void setGameVis(boolean b){
        game.setGameVis(b);
    }

    public String getNnwSize(){
        NeuralNetwork bestNnw = new NeuralNetwork(directoryRoot+"previousBest.txt");
        return "Inputs:" + bestNnw.getNeuralLayers().get(0).getNeurons().size() + ",  Outputs:"+ bestNnw.getNeuralLayers().get(bestNnw.getNeuralLayers().size()-1).getNeurons().size() + ",  Hidden Layers:"+(bestNnw.getNeuralLayers().size()-2) + ",  Hidden Layer Size:"+bestNnw.getNeuralLayers().get(1).getNeurons().size();
    }

    public void doGen() throws InterruptedException {
        NeuralNetwork bestNnw = new NeuralNetwork(directoryRoot+"previousBest.txt");
        int treat = 0;
        int highestTreat = getTotalTreat(bestNnw, game);
        List<NeuralNetwork> currentGen = createGen(bestNnw);
        for (int i = 0; i < currentGen.size(); i++) {
            treat = getTotalTreat(currentGen.get(i), game);
            if(treat >= highestTreat){
                highestTreat=game.getScore();
                bestNnw = currentGen.get(i);
            }
        }
        bestNnw.writeToFile(directoryRoot+"previousBest.txt");
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

    public int getTotalTreat(NeuralNetwork nnw, Game game) throws InterruptedException {
        game.reset();
        int totalTreat = 0;
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
            totalTreat += treat(game);
            if (game.gameVis) {
                //Thread.sleep(10);
            }
            if (Arrays.deepEquals(previuosMovePlayingFeild, game.getPlayingField())){
                return totalTreat+game.getScore();
            }
        }
        return totalTreat+game.getScore();
    }

    public static List<Double> blockValueToNeuronInput(int[][] values){
        return blockValueToNeuronInput(Arrays.stream(values)
                .flatMapToInt(Arrays::stream)
                .boxed()
                .toList());
    }

    public static List<Double> blockValueToNeuronInput(List<Integer> values){
        List<Double> neuronInputs = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if(values.get(i) != 0){
                neuronInputs.add(((Math.log(values.get(i)) / Math.log(2)) / 17)+1);
            }else {
                neuronInputs.add((double)0);
            }
        }
        return neuronInputs;
    }

    List<NeuralNetwork> createGen(NeuralNetwork nnw){
        List<NeuralNetwork> l = new ArrayList<>();
        NeuralNetwork temp = nnw;
        for (int i = 0; i < generationSize; i++) {
            temp = nnw;
            temp.mutate(mutationRate);
            l.add(temp);
        }
        return l;
    }

    public boolean fileGenerator() throws IOException {
        int nr = 0;
        new File(directoryRoot + "nnwsV2").mkdirs();
        File[] listOfFiles = new File(directoryRoot + "nnwsV2/").listFiles();
        if(listOfFiles != null && listOfFiles.length != 0){
            nr = Integer.parseInt(listOfFiles[listOfFiles.length-1].getName().replace("nnw",""));
            new File(directoryRoot + "nnwsV2/nnw"+(nr+1)).mkdirs();
        }else{
            new File(directoryRoot + "nnwsV2/nnw1").mkdirs();
        }
        File file = new File(directoryRoot + "nnwsV2/nnw"+(nr+1)+"/alltimeBest.txt");
        file.createNewFile();
        file = new File(directoryRoot + "nnwsV2/nnw"+(nr+1)+"/previousBest.txt");
        file.createNewFile();
        directoryRoot += "nnwsV2/nnw"+(nr+1)+"/";
        return true;
    }
}
